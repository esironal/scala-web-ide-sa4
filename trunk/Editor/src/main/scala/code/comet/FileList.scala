package code.comet
import net.liftweb.http._
import code.snippet.FileIn
import code.model.Project
import scala.collection.mutable.HashMap

object Id {
  var _next = -1
  def next = {
    _next += 1
    _next
  }
}

class FileList extends CometActor with CometListener {

    val formId: String = Id.next.toString

    var dirName: String = ""

    def registerWith = FileManager

    override def lowPriority = {
        case dir: HashMap[String,String] => {
          dir.get(formId) match {
            case Some(d) => dirName = d
            case None =>
          }
          reRender()
        }
        case _ =>
          reRender()
    }

    def render = {
        "#dirName" #> dirName &
        "li *" #> FileIn.listFiles(name.open_!, dirName, formId) &
        "#projectName" #> Project.getProjectByIdAndByCurrentUser(name.open_!).name.is

    }

}
