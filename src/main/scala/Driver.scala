import java.io.{File, FileWriter}
import java.util.Date


object Driver extends App {
  val folder = new File("src/test/resources")
  //    val map = new util.HashMap[String, String]
  val watcher = new FileWatcher(folder)
  watcher.addListener(new FileAdapter() {
    override def onModified(event: FileEvent): Unit = {
      System.out.println("Modified " + new Date(event.getFile.lastModified))
    }
  }).watch()

  System.out.println("Starting")
  val file = new File(folder + "/test.txt")
  try {
    val writer = new FileWriter(file)
    try {
      writer.write("Some String")
      Thread.sleep(2000)
      writer.append("Parth")
    } finally if (writer != null) writer.close()
  }
  Thread.sleep(500)
  System.out.println("Final: " + new Date(file.lastModified()))
}
