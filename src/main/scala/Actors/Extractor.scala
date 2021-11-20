package Actors

import akka.actor.Actor

import java.io.File
import java.nio.file.Files
import scala.io.Source

class Extractor extends Actor {
  var lastReadLines: scala.collection.mutable.Map[String, Int] = scala.collection.mutable.Map[String, Int]()
  override def receive: Receive = {
    case file: File =>
      if (!lastReadLines.contains(file.getName)) lastReadLines += (file.getName -> 0)
      val BufferedSource = Source.fromFile(file)
      val data = Files.lines(file.toPath)
      data.skip(lastReadLines(file.getName)).forEach(println)
      lastReadLines(file.getName) = BufferedSource.getLines.size
  }
}