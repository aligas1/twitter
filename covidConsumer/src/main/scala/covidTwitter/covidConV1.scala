package covidTwitter

import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

object covidConV1 {

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

  val stream = spark
    .readStream
    .format("kafka")
    .option("kafka.bootstrap.servers", "localhost:9092")
    .option("subscribe", "topicName")
    .load()

  stream.selectExpr("CAST(key as STRING)", "CAST(key as STRING)")
    .as[(String, String)]

  // stream.writeStream
  // to Hbase? need to import Hbase connection
}
