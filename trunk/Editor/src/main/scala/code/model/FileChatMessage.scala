package code
package model

import net.liftweb.mapper._
import net.liftweb.util._
import net.liftweb.common._

import java.math.MathContext

object FileChatMessage extends FileChatMessage with LongKeyedMetaMapper[FileChatMessage] {
	override def dbTableName = "FileChatMessages"
	
	override def fieldOrder = List(id, projectID, fileName, timeStamp, text)
}

class FileChatMessage extends LongKeyedMapper[FileChatMessage] with IdPK {
	
	def getSingleton = FileChatMessage

	
	object fileName extends MappedString(this, 150) {
		override def displayName = "Project ID"
	}
	
	
	object projectID extends MappedLong(this) {
		override def displayName = "Project ID"
	}
	
	object timeStamp extends MappedDateTime(this){
		override def displayName = "timeStamp"
	}
	
	 object text extends MappedTextarea(this, 2048) {
   
		 override def displayName = "Message"
  }
	
	
	

}