package code
package snippet

import net.liftweb._
import http._
import js._
import JsCmds._
import JE._
import util.Helpers._
import comet.ChatServer
import java.io._;
import java.util._;
import net.liftweb.http.js.JE._
import scala.xml._
import code.model.{Project,User}
import code.comet.FileManager
import net.liftweb.mapper._

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
object Download {
	
	def zip(projectId : String) : Boolean = {
		val fullProjectPath = Project.find(By(Project.id,java.lang.Long.parseLong(projectId))).open_!.path 
		val c = scala.Array("/bin/bash","-c", "zip -r ./src/main/webapp/download/"+ projectId + ".zip "+ "./projectFiles/" + projectId)
    	val exec = Runtime.getRuntime.exec(c)  		
  		exec.waitFor()
  		val v = exec.exitValue()
        v == 0
    }  	

	
	def getlink = {
		val id = S.param("id").open_!
		
		"a" #> SHtml.link("download", () => {
	  	  val result = zip(id)
		  if(result){
			S.redirectTo("/download/" + id + ".zip")
		  } else {
			S.error("Could not create file " + id + ".zip")
		  }
	    }, <span>Download</span>)
	} 
}
