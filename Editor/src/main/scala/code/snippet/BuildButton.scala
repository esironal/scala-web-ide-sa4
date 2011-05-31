package code
package snippet

import net.liftweb._
import http._
import common._
import js._
import JsCmds._
import JE._

import S._
import util.Helpers._
import scala.xml._

object BuildButton extends StatefulSnippet {
  private var searchString = ""
  private var result: Box[NodeSeq] = Empty

  val dispatch: DispatchIt = { case a:String => query(a) }

  def query(log: String) = {
      result = Full(log)

    
    "#log" #> result.openOr(<p>Nothing</p>)
  }
}
