package covidTwitter

import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkConf
import org.apache.spark.sql
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types.{LongType, StructField, StructType}


object covidConV2 {

  def main(args: Array[String]): Unit = {

    println("Hello")

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

    // read schema as json from mini batch 
    val smallBatchSchema = spark.read.json("hdfs://localhost:9000/data/tmp/smallBatch").schema

    println(smallBatchSchema)

    val stream = spark.readStream
      .format(source = "kafka")
      .option("kafka.bootstrap.servers", "localhost:9092")
      .option("subscribe", "covid_v1")
      .load()

    import spark.implicits._

    val dataDf = stream.selectExpr("CAST(value AS STRING) as json")
      .select(from_json($"json", schema=smallBatchSchema).as("data"))
      .select("data.user", "data.id", "data.lang", "data.created_at", "data.text")

    val query = dataDf.writeStream
      .format("console")
      .start()

    query.awaitTermination()







    // "created_at", "id", "text", "lang", "user" - nested and "entities" is nested but holds hashtag list

//    case class NewDfSchema(created_at: String, id: Long, text: String, lang: String, )
//
//    case class UserSchema(
//                     user: StructType(StructField(contributors_enabled,BooleanType,true),
//    StructField(user,StructType(StructField(contributors_enabled,BooleanType,true),
//      StructField(created_at,StringType,true), StructField(default_profile,BooleanType,true),
//      StructField(default_profile_image,BooleanType,true), StructField(description,StringType,true),
//      StructField(favourites_count,LongType,true), StructField(follow_request_sent,StringType,true),
//      StructField(followers_count,LongType,true), StructField(following,StringType,true),
//      StructField(friends_count,LongType,true), StructField(geo_enabled,BooleanType,true),
//      StructField(id,LongType,true), StructField(id_str,StringType,true),
//      StructField(is_translator,BooleanType,true), StructField(lang,StringType,true),
//      StructField(listed_count,LongType,true), StructField(location,StringType,true),
//      StructField(name,StringType,true), StructField(notifications,StringType,true),
//      StructField(profile_background_color,StringType,true), StructField(profile_background_image_url,StringType,true),
//      StructField(profile_background_image_url_https,StringType,true), StructField(profile_background_tile,BooleanType,true),
//      StructField(profile_banner_url,StringType,true), StructField(profile_image_url,StringType,true), StructField(profile_image_url_https,StringType,true),
//      StructField(profile_link_color,StringType,true), StructField(profile_sidebar_border_color,StringType,true),
//      StructField(profile_sidebar_fill_color,StringType,true), StructField(profile_text_color,StringType,true),
//      StructField(profile_use_background_image,BooleanType,true), StructField(protected,BooleanType,true),
//    StructField(screen_name,StringType,true), StructField(statuses_count,LongType,true),
//    StructField(time_zone,StringType,true), StructField(translator_type,StringType,true), StructField(url,StringType,true),
//    StructField(utc_offset,StringType,true),
//    StructField(verified,BooleanType,true)),true)),true), StructField(source,StringType,true),
//    StructField(text,StringType,true), StructField(timestamp_ms,StringType,true), StructField(truncated,BooleanType,true)
//    , StructField(user,StructType(StructField(contributors_enabled,BooleanType,true),
//      StructField(created_at,StringType,true), StructField(default_profile,BooleanType,true),
//      StructField(default_profile_image,BooleanType,true), StructField(description,StringType,true),
//      StructField(favourites_count,LongType,true), StructField(follow_request_sent,StringType,true), StructField(followers_count,LongType,true),
//      StructField(following,StringType,true), StructField(friends_count,LongType,true), StructField(geo_enabled,BooleanType,true),
//      StructField(id,LongType,true), StructField(id_str,StringType,true), StructField(is_translator,BooleanType,true), StructField(lang,StringType,true),
//      StructField(listed_count,LongType,true), StructField(location,StringType,true), StructField(name,StringType,true),
//      StructField(notifications,StringType,true), StructField(profile_background_color,StringType,true),
//      StructField(profile_background_image_url,StringType,true), StructField(profile_background_image_url_https,StringType,true),
//      StructField(profile_background_tile,BooleanType,true), StructField(profile_banner_url,StringType,true),
//      StructField(profile_image_url,StringType,true), StructField(profile_image_url_https,StringType,true),
//      StructField(profile_link_color,StringType,true), StructField(profile_sidebar_border_color,StringType,true),
//      StructField(profile_sidebar_fill_color,StringType,true), StructField(profile_text_color,StringType,true),
//      StructField(profile_use_background_image,BooleanType,true), StructField(protected,BooleanType,true),
//    StructField(screen_name,StringType,true), StructField(statuses_count,LongType,true),
//    StructField(time_zone,StringType,true), StructField(translator_type,StringType,true),
//    StructField(url,StringType,true), StructField(utc_offset,StringType,true), StructField(verified,BooleanType,true)),true))


  }


}
