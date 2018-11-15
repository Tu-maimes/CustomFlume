package com.ysstest.spark

import org.apache.spark.sql.SparkSession

/**
  * @author wangshuai
  * @version 2018-10-22 09:25
  *          describe: 
  *          目标文件：
  *          目标表：
  */
object SparkTest {
  def main(args: Array[String]): Unit = {
//    val sparkSession = SparkSession.builder().master("local[*]").appName("统计数据的条数").getOrCreate()
//    val frame = sparkSession.read.text("/yss/guzhi/interface/20181114/sjsmx")
//    println(frame.count())

    println(LoadResource.driver)
  }
}
