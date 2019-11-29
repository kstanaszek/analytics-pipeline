package com.gmail.kstanaszek

import com.gmail.kstanaszek.merchants.amazon.CustomerPipeline
import org.apache.commons.lang.time.DurationFormatUtils
import org.apache.spark.sql.SparkSession


object Main extends Serializable {
  val mongoUrl = "mongodb://127.0.0.1/test.Amazon"

  def main(args: Array[String]) = {

    val name = "Example Application"

    val spark: SparkSession = SparkSession.builder
      .master("local[*]")
      .config("spark.mongodb.input.uri", mongoUrl)
      .config("spark.mongodb.output.uri", mongoUrl)
      .config("spark.driver.memory", "12G")
      .config("spark.executor.memory", "10G")
      .config("partitionSizeMB", "1G")
      .config("spark.mongodb.input.partitioner", "MongoShardedPartitioner")
      .appName(name)
      .getOrCreate()

    val start = System.currentTimeMillis
    CustomerPipeline.run(spark)
    println("Processing time: %1s".format(DurationFormatUtils.formatDuration(System.currentTimeMillis - start, "HH:mm:ss.S")))

  }
}
