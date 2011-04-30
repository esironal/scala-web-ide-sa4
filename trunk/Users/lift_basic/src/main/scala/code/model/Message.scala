package code
package model

import net.liftweb.mapper._
import net.liftweb.util._
import net.liftweb.common._

import java.math.MathContext

object Message extends Message with LongKeyedMetaMapper[Message] {

}

class Message extends LongKeyedMapper[Message] with IdPK {
	
	def getSingleton = Message

	object userID extends MappedLong(this)
	
	object messageNumber extends MappedDecimal(this, MathContext.DECIMAL64, 0)

	object fromID extends MappedString(this, 50)
	
	object date extends MappedDateTime(this)
	
	 object text extends MappedTextarea(this, 2048) {
    override def textareaRows  = 10
    override def textareaCols = 50
    override def displayName = "Message"
  }
}