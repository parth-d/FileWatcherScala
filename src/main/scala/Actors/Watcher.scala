package Actors

import FileDriver.{FileAdapter, FileEvent, FileWatcher}
import akka.actor.{Actor, ActorRef, ActorSystem, Props}

import java.io.File

object Watcher {
  def props(extractor: ActorRef, path: String): Props = Props(new Watcher(extractor, path))
}

class Watcher(extractor: ActorRef, path: String) extends Actor {
  override def receive: Receive = {
    case "watch" =>
      watch(extractor)
    case _ => println("Invalid input. Please check")
  }

  def watch(extractor: ActorRef): Unit = {
    //Specify the path here
    val folder = new File(path)
    val watcher = new FileWatcher(folder)
    watcher.addListener(new FileAdapter() {
      override def onModified(event: FileEvent): Unit = {
        extractor ! event.getFile
      }
    }).watch()
  }
}

object Main extends App {
  val path = args(0)
  val system = ActorSystem("Watchers")
  val extractor = system.actorOf(Props[Extractor], name = "extractor")
  val watcher = system.actorOf(Watcher.props(extractor, path), name = "watcher")
  watcher ! "watch"
}