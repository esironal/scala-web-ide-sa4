package code
package snippet

import net.liftweb._
import http._
import js._
import JsCmds._
import JE._
import code.model.User

object LoginRedirect {

  def render = {
	  
	 if(User.loggedIn_?)  {
		 S.redirectTo("/profile/"+User.currentUser.openOr(null).accountID.is)
	 }
	 else {
		 
		 S.redirectTo("/user_mgt/login")
	 }
  }
}