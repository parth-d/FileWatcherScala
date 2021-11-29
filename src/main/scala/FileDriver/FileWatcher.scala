package FileDriver

import FileDriver.FileWatcher.logger

import java.io.{File, IOException}
import java.nio.file.StandardWatchEventKinds._
import java.nio.file._
import java.util
import java.util.Collections
import java.util.logging.Logger
import scala.jdk.CollectionConverters._


object FileWatcher {
  protected val watchServices = new util.ArrayList[WatchService]
  val logger = Logger.getLogger(FileWatcher.getClass.getName)

  def getWatchServices: util.List[WatchService] = Collections.unmodifiableList(watchServices)
}

class FileWatcher(val folder: File) extends Runnable {

  protected val listeners = new util.ArrayList[FileListener]

  def watch(): Unit = {
    if (folder.exists) {
      val thread = new Thread(this)
      thread.setDaemon(true)
      thread.start()
    }
  }

  override def run(): Unit = {
    try {
      val watchService = FileSystems.getDefault.newWatchService
      try {
        val path = Paths.get(folder.getAbsolutePath)
        path.register(watchService, ENTRY_MODIFY)
        FileWatcher.watchServices.add(watchService)
        var poll = true
        while ( {
          poll
        }) poll = pollEvents(watchService)
      } catch {
        case _: IOException | _: InterruptedException | _: ClosedWatchServiceException =>
          Thread.currentThread.interrupt()
      } finally if (watchService != null) watchService.close()
    }
  }

  @throws[InterruptedException]
  protected def pollEvents(watchService: WatchService): Boolean = {
    val key = watchService.take
    val path = key.watchable.asInstanceOf[Path]
    key.pollEvents.asScala
      .foreach(event => notifyListeners(event.kind, path.resolve(event.context.asInstanceOf[Path]).toFile))
    key.reset
  }

  protected def notifyListeners(kind: WatchEvent.Kind[_], file: File): Unit = {
    val event = new FileEvent(file)
    logger.info("Event received:" + kind.name())
    if (kind eq ENTRY_MODIFY) {
      listeners.asScala
        .foreach(listener => listener.onModified(event))
    }
  }

  def addListener(listener: FileListener): FileWatcher = {
    listeners.add(listener)
    this
  }

  def removeListener(listener: FileListener): FileWatcher = {
    listeners.remove(listener)
    this
  }

  def getListeners: util.List[FileListener] = listeners

  def setListeners(listeners: util.ArrayList[FileListener]): FileWatcher = {
    listeners.forEach(listener => this.listeners.add(listener))
    this
  }
}