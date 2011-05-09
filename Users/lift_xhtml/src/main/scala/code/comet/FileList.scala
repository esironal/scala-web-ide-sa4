package code.comet
import net.liftweb.http._
import code.snippet.FileIn
import code.model.Project

class FileList extends CometActor with CometListener {

    def registerWith = FileManager

    override def lowPriority = {
        case _ => reRender()
    }

    def render = {
        "li *" #> FileIn.listFiles(name.open_!) &
            "#projectName" #> Project.getProjectByIdAndByCurrentUser(name.open_!).name.is

    }

}