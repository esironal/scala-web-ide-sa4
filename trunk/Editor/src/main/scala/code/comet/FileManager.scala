package code.comet
import net.liftweb.http.ListenerManager
import net.liftweb.actor.LiftActor
import java.io.File
import java.io.PrintWriter
import scala.io.Source
import code.model.User
import net.liftweb.http.S
import code.model.Project

object FileManager extends LiftActor with ListenerManager {

    def fileList(projectPath:String): List[String] = {
        (new File(projectPath).listFiles.map(_.getPath)).toList
    }

    def newFile(projectPath:String, name: String) = {
    	new File(projectPath, name).createNewFile
    }

    def saveFile(filePath: String, content: String) = {
        val writer = new PrintWriter(new File(filePath))
        try { writer.print(content) } finally { writer.close() }
    }

    def deleteFile(filePath: String) = {
    	new File(filePath).delete
    }

    def openFile(filePath: String): String = {
    	Source.fromFile(new File(filePath)).getLines.mkString("\n")
    }

    def createUpdate = {}

    override def lowPriority = {
        case ('new, projectPath: String, s: String) => newFile(projectPath, s); updateListeners()
        case ('delete, filePath: String) => deleteFile(filePath); updateListeners()
        case _ => println("NOTHING")
    }
}