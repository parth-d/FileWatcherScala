package Actors

import akka.actor.Actor
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

import java.io.File
import java.nio.file.Files
import java.util.Properties
import scala.io.Source

class Extractor extends Actor {
  var lastReadLines: scala.collection.mutable.Map[String, Int] = scala.collection.mutable.Map[String, Int]()
  var lineCounter = 0
  val props: Properties = new Properties()
  props.put("bootstrap.servers", "localhost:9092")
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("acks","all")
  val producer = new KafkaProducer[String, String](props)
  val topic = "test"

  override def receive: Receive = {
    case file: File =>
      if (!lastReadLines.contains(file.getName)) lastReadLines += (file.getName -> 0)
      val BufferedSource = Source.fromFile(file)
      val data = Files.lines(file.toPath)
      data.skip(lastReadLines(file.getName)).forEach(kafkaTry(_))
      lastReadLines(file.getName) = BufferedSource.getLines.size
//      kafkaTry()
  }

  def kafkaTry(data: String): Unit = {
    try {
      val record = new ProducerRecord(topic, "key",  data)
      val metadata = producer.send(record)
      printf(s"sent record(key=%s value=%s) " + "meta(partition=%d, offset=%d)\n",
        record.key(), record.value(),
        metadata.get().partition(),
        metadata.get().offset())
    }
    catch {
      case e: Exception => e.printStackTrace()
    }
//    finally producer.close()
  }
}