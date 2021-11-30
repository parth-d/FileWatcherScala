package Actors

import akka.actor.{Actor, Props}

import java.io.File
import scala.sys.process._

object Extractor {
  def props(file: File): Props = Props(new Extractor(file))
}

class Extractor(file: File) extends Actor {
//  val props: Properties = new Properties()
//  props.put("bootstrap.servers", "localhost:9092")
//  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
//  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
//  props.put("acks","all")
//  val producer = new KafkaProducer[String, String](props)
//  val topic = "test"

  override def receive: Receive = {
    case str: String =>
                  println("Parth:\t" + self.path.name + " received: " + str)
                  kafkaTry(str)
  }

  def kafkaTry(boundaries: String): Unit = {
    val firstLine: Int = boundaries.split(" ")(0).toInt + 1
    val lastLine: Int  = boundaries.split(" ")(1).toInt
    val data: String = s"sed -n '$firstLine,$lastLine p' ${file.getAbsolutePath}".!!
    println(data)
//    try {
//      val record = new ProducerRecord(topic, "key",  data)
//      val metadata = producer.send(record)
//      printf(s"sent record(key=%s value=%s) " + "meta(partition=%d, offset=%d)\n",
//        record.key(), record.value(),
//        metadata.get().partition(),
//        metadata.get().offset())
//    }
//    catch {
//      case e: Exception => e.printStackTrace()
//    }
  }
}