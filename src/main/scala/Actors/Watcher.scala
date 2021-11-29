package Actors

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import sys.process._

import java.io.File

object Watcher {
  def props(extractor: ActorRef, file: File): Props = Props(new Watcher(extractor, file))
}

class Watcher(extractor: ActorRef, file: File) extends Actor {
  override def receive: Receive = {
    case "startMonitoring" =>
      println("Parth:\t Received startMonitoring for file " + file)
      watch(extractor)
    case _ => println("Invalid input. Please check")
  }

  def watch(extractor: ActorRef): Unit = {
    while (true){
      ("wc -l " + file.getAbsolutePath).!
      Thread.sleep(500)
    }
  }
}

object Main extends App {
  val path = args(0)
  val folder = new File(path)
  val system = ActorSystem("ActorSystem")
  var actors =  Map[File, (ActorRef, ActorRef)]()
  folder.listFiles().foreach{f =>
    println("Parth:\t Creating actors for file " + f.getName)
    val extractor = system.actorOf(Extractor.props(f), name = f.getName + "Extractor")
    val watcher = system.actorOf(Watcher.props(extractor, f), name = f.getName + "Watcher")
    actors += (f -> (watcher, extractor))
  }
  actors.foreach{entry =>
    entry._2._1 ! "startMonitoring"
  }
}