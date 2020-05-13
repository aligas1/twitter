package covidTwitter

import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

object sample4parsing {

  def main(args: Array[String]): Unit = {

    println("yo")

    // set logger to show errors
    val rootLogger = Logger.getLogger("org")
    rootLogger.setLevel(Level.ERROR)

    // set spark configuration
    val conf = new SparkConf().setAppName("covidConsumer").setMaster("local[*]")
    // enable hbase support??

    // start spark session
    val spark = new SparkSession.Builder()
      .config(conf)
      .getOrCreate()

    import spark.implicits._

    // startingOffsets, use earliest for batch
    val smallbatch = spark.read
      .format(source = "kafka")
      .option("kafka.bootstrap.servers", "localhost:9092")
      .option("subscribe", "covid_v1")
      .option("startingOffsets", "earliest")
      .option("endingOffsets", """{"covid_v1":{"0":1}}""")
      .load()
      .selectExpr("CAST(value AS STRING)")

    smallbatch.write.mode("overwrite").format("text").save("hdfs://localhost:9000/data/tmp")

  }

}
