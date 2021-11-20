package FileDriver

import java.util.EventListener

trait FileListener extends EventListener {
  def onModified(event: FileEvent): Unit
}
