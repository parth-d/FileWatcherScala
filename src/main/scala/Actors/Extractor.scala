package Actors

import akka.actor.{Actor, Props}
import constants.{AppConstants, KafkaConstants, ShellCommands}
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import sun.util.resources.cldr.kam.CalendarData_kam_KE

import java.io.File
import java.util.Properties
import java.util.logging.Logger
import scala.sys.process._

object Extractor {
  def props(file: File): Props = Props(new Extractor(file))
}

class Extractor(file: File) extends Actor {
  val props: Properties = new Properties()
  props.put(KafkaConstants.bootstrapServers_k,  KafkaConstants.bootstrapServers_v)
  props.put(KafkaConstants.keySerializer_k,     KafkaConstants.keySerializer_v)
  props.put(KafkaConstants.valueSerializer_k,   KafkaConstants.valueSerializer_v)
  props.put(KafkaConstants.acks_k,              KafkaConstants.acks_v)

  val producer = new KafkaProducer[String, String](props)
  val logger: Logger = Logger.getLogger(this.getClass.getName)
  val topic: String = KafkaConstants.topicName

  override def receive: Receive = {
    case str: String =>
                  logger.info("Extracting for file: \t" + self.path.name)
                  kafkaTry(str)
  }

  def kafkaTry(boundaries: String): Unit = {
    val firstLine: Int = boundaries.split(" ")(0).toInt + 1
    val lastLine: Int  = boundaries.split(" ")(1).toInt
    val data: String = s"${ShellCommands.sed} '$firstLine,$lastLine p' ${file.getAbsolutePath}".!!
    try {
      val record = new ProducerRecord(topic, "key", data)
      producer.send(record)
    }
    catch {
      case e: Exception => e.printStackTrace()
    }
  }
}
