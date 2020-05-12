package covidTwitter

import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.{col, from_json}
import org.apache.spark.sql.types._


object covidConV1 {

  def main(args: Array[String]): Unit = {
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

    val stream = spark.readStream
      .format(source = "kafka")
      .option("kafka.bootstrap.servers", "localhost:9092")
      .option("subscribe", "covid_v1")
      .load()

    val stream2 = stream.selectExpr("CAST(value AS STRING)")

    val query = stream2.writeStream
      .format("console")
      .start()


    query.awaitTermination()

  }

  // stream.writeStream
  // to Hbase? need to import Hbase connection
}
