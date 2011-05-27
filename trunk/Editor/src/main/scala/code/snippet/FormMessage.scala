package code 
package snippet 

import net.liftweb.util._
import net.liftweb._
import net.liftweb.common._
import java.util.Date
import code.lib._
import http._
import util._
import Helpers._
import js._
import JsCmds._
import JE._
import common._
import sitemap._
import Loc._
import mapper._
import scala.xml._
import code.model.ProfileMessage
import code.model.User

object FormMessage extends LiftScreen {
	object message extends ScreenVar(ProfileMessage.create)
	
	override def finishButton: Elem = <button class="msgButton">{S.??("Post")}</button>
	override def cancelButton: Elem = <div style="visibility:hidden;">Hidden</div>
	addFields(() => message.is.text)

	def finish() {
		val user = S.request.openOr(null).path(1).toString
  		val userInstance: User = User.find(By(User.accountID, user)).openOr(null)


		message.is.userID(userInstance.id.is)
		message.is.fromID(User.currentUser.openOr(null).id.is)
		message.is.messageNumber(ProfileMessage.findAll(By(ProfileMessage.userID, userInstance.id.is)).length + 1)
		message.is.date(new java.util.Date)
		message.is.save
		
		S.redirectTo("/profile/" + user)
	}
}