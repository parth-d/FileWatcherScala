package Actors

import Watcher._
import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import constants.{ActorConstants, AppConstants, ShellCommands}
import constants.AppConstants.START_MONITORING

import java.io.File
import java.util.logging.Logger
import javax.xml.stream.events.StartDocument
import scala.annotation.tailrec
import scala.sys.process._

object Watcher {
  def props(extractor: ActorRef, file: File): Props = Props(new Watcher(extractor, file))
}

class Watcher(extractor: ActorRef, file: File) extends Actor {

  val logger: Logger = Logger.getLogger(this.getClass.getName)

  override def receive: Receive = {
    case START_MONITORING =>
      logger.info("Received startMonitoring for file " + file)
      watch(extractor, 0)
    case _ => logger.severe("Invalid input. Please check: ")
  }

  @tailrec
  private def watch(extractor: ActorRef, lastReadLine: Int): Unit = {
    val newLine: Int = (ShellCommands.wc + file.getAbsolutePath).!!.split(" ")(0).toInt
    if (newLine != lastReadLine) {
      logger.info("Changes observed, sending message to extractor " + extractor.path.name)
      extractor ! lastReadLine + " " + newLine
    }
    watch(extractor, newLine)
  }
}

object Main extends App {
  val path = args(0)
  val folder = new File(path)
  val system = ActorSystem("ActorSystem")
  val actors = scala.collection.mutable.Map[File, (ActorRef, ActorRef)]()
  val logger : Logger = Logger.getLogger(this.getClass.getName)
  folder.listFiles().foreach { f =>
    logger.info("Creating actors for file " + f.getName)
    val extractor = system.actorOf(Extractor.props(f), name = ActorConstants.extractorSubName + f.getName)
    val watcher = system.actorOf(Watcher.props(extractor, f), name = ActorConstants.watcherSubName + f.getName)
    actors += (f -> (watcher, extractor))
  }
  actors.foreach { entry =>
    entry._2._1 ! START_MONITORING
  }
}