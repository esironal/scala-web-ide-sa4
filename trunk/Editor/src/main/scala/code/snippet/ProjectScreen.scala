package code.snippet
import net.liftweb.http.LiftScreen
import net.liftweb.http.S
import code.model.User
import net.liftweb.common.Full
import code.model.Project
import java.io.File

object ProjectScreen extends LiftScreen {

	val name = field("Name", "")
	
	def finish = {
		User.currentUser match {
			case Full(user) => {
				val newProject = Project.create
				newProject.name(name)
				newProject.save
				
				new File(newProject.path).mkdir
				
				user.projects += newProject
				user.save
			}
			case _ => S.error("You must be logged in")
		}
		S.redirectTo("/projectList/")
	}
}