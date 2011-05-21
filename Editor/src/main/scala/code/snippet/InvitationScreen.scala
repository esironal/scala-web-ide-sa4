package code.snippet
import net.liftweb.http.LiftScreen
import net.liftweb.http.S
import code.model.User
import net.liftweb.common.Full
import code.model.Project
import java.io.File
import scala.xml.NodeSeq
import net.liftweb.mapper.By

object InvitationScreen {

    def render(in: NodeSeq): NodeSeq = {

        for {
            r <- S.request if r.post_?
            email <- S.param("email")
            id <- S.param("id")
            currentUser <- User.currentUser
        } {
            val currentProject = Project.getProjectByIdAndByCurrentUser(id)
            val userToAdd = User.find(By(User.accountID, email))

            userToAdd match {
                case Full(user) => {
                    currentProject.users += user
                    currentProject.save
                }
                case _ => S.error("user not found")
            }

            S.redirectTo("/invite?id="+id)
        }

        in
    }
}