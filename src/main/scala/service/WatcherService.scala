package service

import akka.actor.ActorRef
import constants.ShellCommands

import java.io.File
import java.util.logging.{Level, Logger}
import scala.annotation.tailrec
import sys.process._

class WatcherService {

  val logger: Logger = Logger.getLogger(this.getClass.getName)

  @tailrec
  final def watch(extractor: ActorRef, file: File, lastReadLine: Int): Unit = {
    val newLine: Int = (ShellCommands.wc + file.getAbsolutePath).!!.split(" ")(0).toInt
    if (newLine != lastReadLine) {
      logger.log(Level.FINE, "Changes observed, sending message to extractor " + extractor.path.name)
      extractor ! lastReadLine + " " + newLine
    }
    watch(extractor, file, newLine)
  }
}