package code.snippet
import net.liftweb.http._
import net.liftweb.common._
import net.liftweb.http.js.JsCmds._
import code.model.User
import code.model.Project
import scala.xml.Text
import scala.xml.NodeSeq

object ProjectUsers {

    def listProjectUsers(in: NodeSeq): NodeSeq = {
        User.currentUser match {
            case Full(user) =>
                <ul class="list">{
                    val projectId = S.param("id").open_!.toString
                    val users = Project.getProjectByIdAndByCurrentUser(projectId).users
                    users.map(user => <li>{ user.accountID.is }</li>)
                }</ul>
            case _ => Text("not logged in")
        }
    }

}
