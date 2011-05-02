package code
package snippet

import net.liftweb._
import http._
import js._
import JsCmds._
import JE._
import util.Helpers._
import comet.ChatServer

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

private var filename = ""
private var name = ""
private var message = "" 
  
  def render = {
  	
  	def process() = {
	    ChatServer ! (("[" + name + "] " + message, filename))
	    SetValById("chat_in", "")
  	}
  	
  	def processFileName(s:String) = {
		filename = s
  	}
  	
  	def processName(s:String) = {
		name = s
  	}
  	
  "name=filename" #> SHtml.onSubmit(processFileName) &
  "name=name" #> SHtml.onSubmit(processName) &
  "#chat_in" #> SHtml.onSubmit(message = _) &
  "type=submit" #> SHtml.onSubmitUnit(process)    
  	
  }
}
