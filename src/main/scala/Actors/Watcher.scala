package Actors

import FileDriver.{FileAdapter, FileEvent, FileWatcher}
import akka.actor.{Actor, ActorRef, ActorSystem, Props}

import java.io.File

object Watcher {
  def props(extractor: ActorRef): Props = Props(new Watcher(extractor))
}

class Watcher(extractor: ActorRef) extends Actor {
  override def receive: Receive = {
    case "watch" =>
      watch(extractor)
    case _ => println("Invalid input. Please check")
  }

  def watch(extractor: ActorRef): Unit = {
    //Specify the path here
    val folder = new File("src/test/resources")
    val watcher = new FileWatcher(folder)
    watcher.addListener(new FileAdapter() {
      override def onModified(event: FileEvent): Unit = {
        extractor ! event.getFile
      }
    }).watch()
  }
}

object Main extends App {
  val system = ActorSystem("Watchers")
  val extractor = system.actorOf(Props[Extractor], name = "extractor")
  val watcher = system.actorOf(Watcher.props(extractor), name = "watcher")
  watcher ! "watch"
}