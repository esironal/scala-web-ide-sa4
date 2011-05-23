package code
package snippet

import net.liftweb._
import http._
import js._
import JsCmds._
import JE._
import util.Helpers._

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
    var filename = S.param("filename") openOr "/dev/null"

    def setBuffer(s: String): JsCmd = {
      val buffer = s
      var filename = S.param("filename") openOr "/dev/null"

      import java.io._
      try {
        val out = new PrintStream(new FileOutputStream(filename))
        out.print(buffer)
        out.close

        Alert("Saved " + filename +  " |" + buffer + "|")
      }
      catch {
        case ex: IOException =>
          Alert("Failed " + filename +  " |" + buffer + "|")
      }
    }

    "name=buffer" #> SHtml.textarea("", setBuffer _, "id" -> "buffer")
  }
}
