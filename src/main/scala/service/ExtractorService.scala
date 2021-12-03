package service

import constants.{KafkaConstants, ShellCommands}
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

import java.io.File
import java.util.Properties
import java.util.logging.Logger
import sys.process._

class ExtractorService {

  val props: Properties = new Properties()
  props.put(KafkaConstants.bootstrapServers_k,  KafkaConstants.bootstrapServers_v)
  props.put(KafkaConstants.keySerializer_k,     KafkaConstants.keySerializer_v)
  props.put(KafkaConstants.valueSerializer_k,   KafkaConstants.valueSerializer_v)
  props.put(KafkaConstants.acks_k,              KafkaConstants.acks_v)

  val producer = new KafkaProducer[String, String](props)
  val logger: Logger = Logger.getLogger(this.getClass.getName)
  val topic: String = KafkaConstants.topicName

  def kafkaTry(boundaries: String, file: File): Unit = {
    val firstLine: Int = boundaries.split(" ")(0).toInt + 1
    val lastLine: Int  = boundaries.split(" ")(1).toInt
    val data: String = s"${ShellCommands.sed} '$firstLine,$lastLine p' ${file.getAbsolutePath}".!!
    try {
      val record = new ProducerRecord(topic, "key", data)
      logger.info(data)
//      producer.send(record)
    }
    catch {
      case e: Exception => e.printStackTrace()
    }
  }
}
