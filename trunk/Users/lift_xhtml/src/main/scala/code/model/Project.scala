package code
package model

import net.liftweb.mapper._
import net.liftweb.util._
import net.liftweb.common._

import java.math.MathContext

object Project extends Project with LongKeyedMetaMapper[Project] {
	override def dbTableName = "projects"
	
	override def fieldOrder = List(id, projectPath)
}

class Project extends LongKeyedMapper[Project] with IdPK {
	
	def getSingleton = Project

	object projectPath extends MappedString(this, 150){
		override def displayName = "Project Path"
	}
	

}