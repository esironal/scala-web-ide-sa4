package code
package snippet

import net.liftweb._
import http._
import js._
import JsCmds._
import JE._
import util.Helpers._

import code.comet.FileManager
import code.model.Project

/**
 * A snippet transforms input to output... it transforms
 * templates to dynamic content.  Lift's templates can invoke
 * snippets and the snippets are resolved in many different
 * ways including "by convention".  The snippet package
 * has named snippets and those snippets can be classes
 * that are instantiated when invoked or they can be
 * objects, singletons.  Singletons are useful if there's
 * no explicit state managed in the snippet.
 */
object SaveBuffer {

  def render = {	
    def setBuffer(buffer: String): JsCmd = {
      val projectId = S.param("projectId").open_!.toString
      val projectPath = Project.getProjectByIdAndByCurrentUser(projectId).path
      val filePathInProject = S.param("filename").open_!.toString
      try{
      	FileManager.saveFile(projectPath + filePathInProject, buffer)
      	Alert("File saved successfully!")
      }catch {
      	case ex: java.io.IOException =>
      		Alert("IOException " + filePathInProject +  " |" + buffer + "|")
      }
    }

    "name=buffer" #> SHtml.textarea("", setBuffer _, "id" -> "buffer")
  }
}
