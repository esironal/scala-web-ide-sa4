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
	
	object profile extends MappedString(this, 30) 
	
	
	object userID extends MappedDecimal(this, MathContext.DECIMAL64, 0)
	
	object messageNumber extends MappedString(this, 30)
	
	object from extends MappedString(this, 30)
	
	object date extends MappedDateTime(this)
	
	object text extends MappedString(this, 150)
	
	
	
	
					
}	
