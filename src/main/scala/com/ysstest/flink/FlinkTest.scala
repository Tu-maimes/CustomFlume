package com.ysstest.flink

import org.apache.flink.api.java.utils.ParameterTool
import org.apache.flink.api.scala.ExecutionEnvironment
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.api.scala.createTypeInformation
import org.apache.flink.streaming.api.windowing.time.Time

/**
  * @author wangshuai
  * @version 2018-10-10 14:23
  *          describe: 
  *          目标文件：
  *          目标表：
  */
case class WordWithCount(word: String, count: Long)

object FlinkTest {


  def main(args: Array[String]): Unit = {
    val port: Int = try {
      ParameterTool.fromArgs(args).getInt("port")
    } catch {
      case e: Exception => {
        System.err.println("No port specified. Please run 'SocketWindowWordCount --port <port>'")
        return
      }
    }
    val environment = StreamExecutionEnvironment.getExecutionEnvironment
    val text = environment.socketTextStream("192.168.102.114", port)
    val windowCounts = text
      .flatMap { w => w.split("\\s") }
      .map { w => WordWithCount(w, 1) }
      .keyBy("word")
      .timeWindow(Time.seconds(5), Time.seconds(1))
      .sum("count")
    windowCounts.print().setParallelism(5)

    val value = environment.readTextFile("")




    environment.execute()
  }

}
