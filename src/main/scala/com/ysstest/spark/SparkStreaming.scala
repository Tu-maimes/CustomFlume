package com.ysstest.spark

import java.io.File

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * @author wangshuai
  * @version 2018-09-27 09:31
  *          describe: 
  *          目标文件：
  *          目标表：
  */
object SparkStreaming {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("spark接受kafka数据").setMaster("local[*]")
    val ssc = new StreamingContext(conf, Seconds(1))
    val spark = SparkSession.builder().config(conf).getOrCreate()
    import spark.implicits._
    val rdd = KafkaUtilsSpark.getStream(ssc)
    //    rdd.foreachRDD(x => x.foreach(println(_)))
    //    val value = rdd.filter(x => x._1 == 0).filter(_.fileName.contains("gh"))
    val rowValue = rdd.filter(x => x.currentRecord != 1).filter(_.fileName.contains("shghtest"))
    rowValue.foreachRDD(x=>x.foreach(println(_)))
//    rowValue.foreachRDD(x => x.foreach(a=>{
//      val strings = a.rowValue.split("\n")
//      strings.foreach(value=>{
//        val strs = value.split(",")
//        strs
//      })
//    }))
    ssc.start()
    ssc.awaitTermination()


  }
}
