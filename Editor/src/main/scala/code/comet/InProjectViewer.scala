package code
package comet

import net.liftweb._
import http._
import js._
import JsCmds._
import JE._
import util.Helpers._
import scala.xml.NodeSeq
import common._
import mapper._
import scala.xml.{ Node, NodeSeq, XML }

import scala.collection.mutable.Set
import scala.collection.mutable.HashMap

import code.model._

class InProjectViewer extends CometActor with CometListener {
	
  val project = java.lang.Long.parseLong(S.param("id").open_!)
	
  var userProject: HashMap[Long, Set[Long]] = new HashMap[Long, Set[Long]]
	
  def registerWith = UserInProjectDispatcher

  override def lowPriority = {
    case list: HashMap[Long, Set[Long]] => {
      userProject = list
      reRender()
    }
  }

  def render = {
  	  "#inProject_out" #> createNodeSeq(userProject)
  }
  
  def createNodeSeq(list: HashMap[Long, Set[Long]]) = {
  		
  		if(userProject.contains(project)) {
  			val userSet = userProject.get(project).get
  	
  			<span> {
  			for(user <- userSet.toList.flatMap(id => User.find(By(User.id,id)))) yield(
						<span>{"#   "}</span>
						<b><a href={"/profile/"+user.accountID.is}>{""+user.accountID.is}</a></b>
						<span>{"   " + user.firstName.is + " " + user.lastName.is}</span>
						<br/>
						)
			}</span>
  		} else {
  			<span></span>
  		}
  }
}