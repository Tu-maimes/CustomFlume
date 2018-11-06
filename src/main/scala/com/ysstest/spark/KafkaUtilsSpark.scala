package com.ysstest.spark

import org.apache.avro.io.DecoderFactory
import org.apache.avro.specific.SpecificDatumReader
import org.apache.avro.util.Utf8
import org.apache.flume.source.avro.AvroFlumeEvent
import org.apache.kafka.common.serialization.{BytesDeserializer, StringDeserializer}
import org.apache.kafka.common.utils.Bytes
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010._

/**
  * @author wangshuai
  * @version 2018-09-27 09:39
  *          describe: 
  *          目标文件：
  *          目标表：
  */
case class KafkaUtilsSpark(fileName: String, rowValue: String, currentRecord: Integer)

object KafkaUtilsSpark {
  val kafkaParams = Map[String, Object](
    "bootstrap.servers" -> " bj-rack001-hadoop004:6667,bj-rack001-hadoop002:6667,bj-rack001-hadoop003:6667",
    "key.deserializer" -> classOf[StringDeserializer],
    "value.deserializer" -> classOf[BytesDeserializer],
    "group.id" -> "ws_1",
    "auto.offset.reset" -> "earliest", //earliest
    "enable.auto.commit" -> (false: java.lang.Boolean)
  )
  val topics = Array("flume_guzhi")
  val reader = new SpecificDatumReader[AvroFlumeEvent](AvroFlumeEvent.SCHEMA$)

  def getStream(ssc: StreamingContext) = {
    val stream = KafkaUtils.createDirectStream[String, Bytes](ssc,
      PreferConsistent,
      Subscribe[String, Bytes](topics, kafkaParams))
    stream.map(record => {
      val body = record.value().get()
      val decoder = DecoderFactory.get().binaryDecoder(body, null)
      val event = reader.read(new AvroFlumeEvent, decoder)
      //      (new String(event.getBody.array()))
      val fileName = event.getHeaders.get(new Utf8("fileName")).toString
      var currentRecord = 0
      try
        currentRecord =
          Integer.valueOf(event.getHeaders.get(new Utf8("currentRecord")).toString)
      catch {
        case ex: NullPointerException =>
      }
      val value = new String(event.getBody.array())
      KafkaUtilsSpark(fileName, value, currentRecord)
    })
  }
}
