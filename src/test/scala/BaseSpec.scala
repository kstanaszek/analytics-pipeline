import com.gmail.kstanaszek.utils.profiler.Logger
import org.apache.spark.sql.{SQLContext, SparkSession}
import org.apache.spark.{SparkConf, SparkContext}
import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers}

abstract class BaseSpec extends FlatSpec with Logger with BeforeAndAfterAll with Matchers {
  private val master = "local"
  private val appName = "testing"
  var spark: SparkSession = _
  var sc: SparkContext = _
  var sqlContext: SQLContext = _

  val conf = new SparkConf()
    .setMaster(master)
    .setAppName(appName)
    .set("spark.driver.allowMultipleContexts","true")

  override protected def beforeAll(): Unit = {
    super.beforeAll()
    spark = SparkSession.builder.config(conf).getOrCreate()
    sc = spark.sparkContext
    sqlContext = spark.sqlContext
  }

  override protected def afterAll(): Unit = {
    try {
      sc.stop()
      sc = null
      sqlContext = null
    } finally {
      super.afterAll()
    }
  }
}

