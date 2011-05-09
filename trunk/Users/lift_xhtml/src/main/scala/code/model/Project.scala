package code
package model

import net.liftweb.mapper._

object Project extends Project with LongKeyedMetaMapper[Project] {
    override def dbTableName = "projects"

    override def fieldOrder = List(id, name)
    
    val basePath = "/tmp/"

    def getProjectByIdAndByCurrentUser(projectId: String) = {
    	val currentUser = User.currentUser.open_! 
        currentUser.projects.find(_.id.is == projectId.toLong).get
    }

}

class Project extends LongKeyedMapper[Project] with IdPK with ManyToMany {
    def getSingleton = Project

    def path = Project.basePath + this.id.toString

    object name extends MappedString(this, 255)

    object users extends MappedManyToMany(UserProject, UserProject.project, UserProject.user, User)
}