package Actors

import akka.actor.{Actor, Props}
import service.ExtractorService

import java.io.File
import java.util.logging.{Level, Logger}

object Extractor {
  def props(extractorService: ExtractorService, file: File): Props = Props(new Extractor(extractorService, file))
}

class Extractor(extractorService: ExtractorService, file: File) extends Actor {

  val logger: Logger = Logger.getLogger(this.getClass.getName)

  override def receive: Receive = {
    case str: String =>
      logger.log(Level.FINE, "Extracting for file: \t" + self.path.name)
      extractorService.getData(str, file).foreach(logger.info)
  }
}
