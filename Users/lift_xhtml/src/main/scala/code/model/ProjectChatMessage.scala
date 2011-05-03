package code
package model

import net.liftweb.mapper._
import net.liftweb.util._
import net.liftweb.common._

import java.math.MathContext

object ProjectChatMessage extends ProjectChatMessage with LongKeyedMetaMapper[ProjectChatMessage]{
	override def dbTableName = "projectChatMessages"
	
	override def fieldOrder = List(id, projectID, timeStamp, userID, text)
}

class ProjectChatMessage extends LongKeyedMapper[ProjectChatMessage] with IdPK {
	
	def getSingleton = ProjectChatMessage

	
	
	object projectID extends MappedLong(this) {
		override def displayName = "Project ID"
	}
	
	object timeStamp extends MappedDateTime(this){
		override def displayName = "timeStamp"
	}
	
	object userID extends MappedLong(this) {
		override def displayName = "User ID"
	}
	
	 object text extends MappedTextarea(this, 2048) {
   
		 override def displayName = "Message"
  }
	
	
	

}