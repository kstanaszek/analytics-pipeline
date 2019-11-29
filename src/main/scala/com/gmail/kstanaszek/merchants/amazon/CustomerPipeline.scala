package com.gmail.kstanaszek.merchants.amazon

import java.nio.file.{Path, Paths}

import com.gmail.kstanaszek.feeds.CustomerFeed
import com.gmail.kstanaszek.pipeline.Pipeline
import com.gmail.kstanaszek.utils.profiler.Logger
import org.apache.commons.io.FilenameUtils
import org.apache.spark.sql._


class CustomerPipeline(spark: SparkSession) extends Pipeline[CustomerFeed] with Logger {

  override def extract(filePath: Path): DataFrame = {
    spark.read
      .parquet(filePath.toAbsolutePath.toString)
  }

  override def transform[GoogleMerchantFeed: Encoder](dataFrame: DataFrame): Dataset[GoogleMerchantFeed] = {
    val isNumeric = spark.udf.register("IsNumeric", (inpColumn: String) => inpColumn.forall(_.isDigit))
    val stringCleaner = spark.udf.register("StringCleaner", (s: String) => s.replaceAll("\"", ""))
    val gbp2sekExchangeRate = 12.4650

    import org.apache.spark.sql.functions._
    import spark.implicits._

    val transformed = dataFrame.select(
      $"item_basic_data.amzn_page_url".alias("link"),
      $"item_basic_data.item_title".alias("title"),
      $"item_basic_data.item_unique_id".alias("id"),
      $"item_basic_data.item_image_url".alias("imageLink"),
      $"item_basic_data.item_long_desc".alias("description"),
      $"item_basic_data.item_category".alias("googleProductCategory"),
      $"item_basic_data.list_price".alias("price"),
      $"item_basic_data.item_image_url_small".alias("additionalImageLink"),
      $"item_basic_data.item_shipping_charge".alias("shipping"),
      $"item_basic_data.tp_used_inventory".alias("availability")
    ).withColumn(
      "title",
      when($"title".isNotNull, stringCleaner($"title")))
      .withColumn(
        "description",
        when($"description".isNotNull, stringCleaner($"description")))
      .withColumn(
        "availability",
        when($"availability" === "In Stock", "in stock")
          .otherwise("out of stock"))
      .withColumn(
        "price",
        when($"price".isNull || !isNumeric($"price"), 0)
          .otherwise(round($"price" * lit(gbp2sekExchangeRate), 2)))
      .withColumn(
        "shipping",
        when($"shipping".isNull || !isNumeric($"shipping"), 0)
          .otherwise(round($"shipping" * lit(gbp2sekExchangeRate), 2))
      )
      .filter("googleProductCategory = 'Book'")

    transformed.as[GoogleMerchantFeed]
  }

  override def load[GoogleMerchantFeed: Encoder](dataset: Dataset[GoogleMerchantFeed], filePath: Path): Unit = {
    dataset.distinct()
      .withColumnRenamed("imageLink", "image_link")
      .withColumnRenamed("additionalImageLink", "additional_image_link")
      .withColumnRenamed("googleProductCategory", "google_product_category")
      .repartition(12)
      .write
      .mode(SaveMode.Overwrite)
      .option("header", "true")
      .option("delimiter", "|")
      .option("quoteAll", true)
      .csv(filePath.toAbsolutePath.toString)

    renameFilesToHumanFriendly(filePath)
  }

  def renameFilesToHumanFriendly(filePath: Path): Unit = {
    import org.apache.hadoop.fs._

    type HadoopPath = org.apache.hadoop.fs.Path
    val sc = spark.sparkContext
    val fs = FileSystem.get(sc.hadoopConfiguration)

    val files = FileSystem.get(sc.hadoopConfiguration).listStatus(new HadoopPath(filePath.toAbsolutePath.toString))

    files.foreach(filename => {
      val fullPath = filename.getPath.toString
      val fileName = filename.getPath.getName
      val fileExt = FilenameUtils.getExtension(fileName)

      if (fileName != "_SUCCESS") {
        val fileRenamed = Paths.get(filename.getPath.getName.split('-').take(2).mkString("-") + "." + fileExt)
        val fileRenamedFullPath = Paths.get(filePath.toAbsolutePath.toString, fileRenamed.getFileName.toString).toAbsolutePath.toString
        fs.rename(new HadoopPath(fullPath), new HadoopPath(fileRenamedFullPath))
      }
    })
  }

  def camelToUnderscores(name: String) = "[A-Z\\d]".r.replaceAllIn(name, { m =>
    "_" + m.group(0).toLowerCase()
  })
}

object CustomerPipeline {
  type T = CustomerFeed
  implicit val encoder: Encoder[T] = Encoders.product[T]

  def run(sparkSession: SparkSession): Unit = {

    val rawFilePath = Paths.get("/home/krzysztof/Downloads/books_feeds/AmazonUK/part-00000-3a29a495-1f0b-437f-9324-88b9d7369a2d-c000.snappy.parquet")
    val processedFilePath = Paths.get("/home/krzysztof/Downloads/books_feeds/AmazonUK/splitted/")

    val amazonPipeline = new CustomerPipeline(sparkSession)

    // Extract
    val extracted = amazonPipeline.extract(rawFilePath)

    // Transform
    val transformed = amazonPipeline.transform(extracted)

    // Load
    amazonPipeline.load(transformed, processedFilePath)
  }
}