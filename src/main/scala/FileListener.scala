import java.util.EventListener

trait FileListener extends EventListener {
  def onCreated(event: FileEvent): Unit
  def onModified(event: FileEvent): Unit
  def onDeleted(event: FileEvent): Unit
}
