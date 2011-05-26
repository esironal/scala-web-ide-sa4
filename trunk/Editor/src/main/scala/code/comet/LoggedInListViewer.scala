package code
package comet

import net.liftweb._
import http._
import util._
import Helpers._
import scala.xml.{ Node, NodeSeq, XML }

import code.model._

class LoggedInListViewer extends CometActor with CometListener {
	
  var loggedInList: List[User] = Nil	
	
  def registerWith = LoggedIn

  override def lowPriority = {
    case list: List[User] => {
      loggedInList = list
      reRender()
    }
  }

  def render = {
  	  "#loggedList_out" #> createNodeSeq(loggedInList)
  }
  
  def createNodeSeq(list: List[User]) = {
  		<span> {
  		for(user <- list) yield(
						<span>{"#   "}</span>
						<b><a href={"/profile/"+user.accountID.is}>{""+user.accountID.is}</a></b>
						<span>{"   " + user.firstName.is + " " + user.lastName.is}</span>
						<br/>
						)
		}</span>
  }
}
