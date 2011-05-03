package code
package model

import net.liftweb.mapper._
import net.liftweb.util._
import net.liftweb.common._

import java.math.MathContext

object UserProject extends UserProject with MetaMapper[UserProject] {
	override def dbTableName = "userProjects"
	
	override def fieldOrder = List(userID, projectID)
}

class UserProject extends Mapper[UserProject] {
	
	def getSingleton = UserProject

	object userID extends MappedLong(this){
		override def displayName = "User ID"
	}
	
	object projectID extends MappedLong(this) {
		override def displayName = "Project ID"
	}
	
	

}