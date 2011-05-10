package code
package model

import net.liftweb.mapper._
import net.liftweb.util._
import net.liftweb.common._

import java.math.MathContext

object ProfileMessage extends ProfileMessage with LongKeyedMetaMapper[ProfileMessage] {
	override def dbTableName = "profileMessages"
	
	override def fieldOrder = List(userID, messageNumber, fromID, date, text)
}

class ProfileMessage extends LongKeyedMapper[ProfileMessage] with IdPK {
	
	def getSingleton = ProfileMessage

	object userID extends MappedLong(this){
		override def displayName = "UserID"
	}
	
	object messageNumber extends MappedDecimal(this, MathContext.DECIMAL64, 0){
		override def displayName = "Message Number"
	}

	object fromID extends MappedLong(this){
		override def displayName = "From UserID"
	}
	
	object date extends MappedDateTime(this){
		override def displayName = "timeStamp"
	}
	
	 object text extends MappedTextarea(this, 2048) {
    override def textareaRows  = 3
    override def textareaCols = 50
    override def displayName = "Message"
  }
}