package code.snippet
import net.liftweb.http.LiftScreen
import net.liftweb.http.S
import code.model.User
import net.liftweb.common.Full
import code.model.Project
import java.io.File
import scala.xml._


object ProjectScreen extends LiftScreen {

	val name = field("Project name", "")
	override def finishButton: Elem = <button class="projButton">{S.??("Create")}</button>
	override def cancelButton: Elem = <div style="visibility:hidden;">Hidden</div>
	
	
	
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
		S.redirectTo("/projectList")
	}
}
