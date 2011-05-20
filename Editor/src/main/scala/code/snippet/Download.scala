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
import java.lang._;
import java.util._;
import net.liftweb.http.js.JE._
import scala.xml._

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

private var filename: String = ""
private var contents: String = "not set yet"
  
  def render = {
  	
  	def process() = {
  		var parts: Array[String] = filename.split(":")
  		var fileN: String = parts(parts.length-1)
  		try{
		    // Create file 
		    var fstream:FileWriter = new FileWriter("./tmp/" + fileN);
		    var out:BufferedWriter = new BufferedWriter(fstream);
		    out.write(contents);
		    //Close the output stream
		    out.close();
	    }catch {
	      	case _ =>
	    }
  	}
  	
  	def processFileName(s:String) = {
		filename = s
  	}
  	def processContents(s:String) = {
		contents = s
  	}
  	
  "name=filename" #> SHtml.onSubmit(processFileName) &
  "class=editor" #> SHtml.onSubmit(processContents) &
  "type=submit" #> SHtml.onSubmitUnit(process)   
  }
}
