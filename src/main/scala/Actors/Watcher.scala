package Actors

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

import java.io.File

object Watcher {
  def props(extractor: ActorRef, file: File): Props = Props(new Watcher(extractor, file))
}

class Watcher(extractor: ActorRef, file: File) extends Actor {
  override def receive: Receive = {
    case "watch" =>
      watch(extractor)
    case _ => println("Invalid input. Please check")
  }

  def watch(extractor: ActorRef): Unit = {
//    //Specify the path here
//    val folder = new File(path)
//    val watcher = new FileWatcher(folder)
//    watcher.addListener(new FileAdapter() {
//      override def onModified(event: FileEvent): Unit = {
//        extractor ! event.getFile
//      }
//    }).watch()
  }
}

object Main extends App {
  val path = args(0)

  val folder = new File(path)
  val system = ActorSystem("ActorSystem")
  var actors =  Map[File, (ActorRef, ActorRef)]()
  folder.listFiles().foreach{f =>
    val extractor = system.actorOf(Extractor.props(f), name = f.getName + "Extractor")
    val watcher = system.actorOf(Watcher.props(extractor, f), name = f.getName + "Watcher")
    actors += (f -> (watcher, extractor))
  }
  println(actors)
}