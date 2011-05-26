package code
package comet

import net.liftweb._
import http._
import actor._

import code.model._

object LoggedIn extends LiftActor with ListenerManager {
	
	var loggedInList: List[User] = Nil 
	
	def createUpdate = loggedInList
	
	override def lowPriority = {
    	case list: List[User] => {
    		loggedInList = list
    		updateListeners();
    	}
	}
}