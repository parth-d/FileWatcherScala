package Actors

import akka.actor.{Actor, ActorRef, Props}
import constants.AppConstants.START_MONITORING
import service.WatcherService

import java.io.File
import java.util.logging.Logger

object Watcher {
  def props(extractor: ActorRef, file: File): Props = Props(new Watcher(extractor, file))
}

class Watcher(extractor: ActorRef, file: File) extends Actor {

  val logger: Logger = Logger.getLogger(this.getClass.getName)

  override def receive: Receive = {
    case START_MONITORING =>
      logger.info("Received startMonitoring for file " + file)
      new WatcherService().watch(extractor, file, 0)
    case _ => logger.severe("Invalid input. Please check: ")
  }
}