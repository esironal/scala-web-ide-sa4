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

import scala.actors.Actor
import scala.actors.Actor._

import model.User

class EnterProjectNotifier extends CometActor {
	
	override def lifespan = Full(5 minutes)
	
	val projectId = java.lang.Long.parseLong(S.param("id").open_!)
	val userId = User.currentUser.open_!.id.is
	
	UserInProjectDispatcher ! (('Enter, projectId, userId))
	
	override def localShutdown() {
		
		UserInProjectDispatcher ! (('Leave, projectId, userId))
		super.localShutdown()
	}
	
	def render = {
		<span></span>
	}
}