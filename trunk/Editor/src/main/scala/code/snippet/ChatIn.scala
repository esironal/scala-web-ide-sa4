package code
package snippet

import net.liftweb._
import http._
import js._
import JsCmds._
import JE._
import util.Helpers._
import comet.ChatServer
import scala.xml.NodeSeq

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
object ChatIn {

  
  def render = {

    var filename = ""
    var name = ""
    var message = ""
    
    def processMessage(s: String) = {
    	message = s
    	filename = S.param("filename").openOr("")
    	name = S.param("name").openOr("")
      	process()
    }
  	
  	def process() = {
	    ChatServer ! (("[" + name + "] " + message, filename))
	    SetValById("chat_in", "") 
//	     Alert("name = " + name + " message = " + message + " filename = " + filename)
  	}
  	
  	def processFileName(s: String) = {
  		filename = s
  	}
  	
  	def processName(s: String) = {
  	    name = s
  	}
  
//    (n: NodeSeq) => {
//    	("name=filename" #> SHtml.hidden(processFileName _, "broken filename", "name" -> "filename") &
//         "name=name" #> SHtml.hidden(processName _, "broken name") &
//         "#chat_in" #> SHtml.onSubmit(message = _))(n) ++ SHtml.hidden(process)
//    }  	

         "#chat_in" #> SHtml.onSubmit(processMessage _)
  }
}
