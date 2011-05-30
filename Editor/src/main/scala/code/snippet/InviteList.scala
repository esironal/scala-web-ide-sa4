package code
package snippet


import net.liftweb.http.LiftScreen
import net.liftweb.http.S
import code.model.User
import net.liftweb.common.Full
import code.model.Project
import java.io.File
import scala.xml.NodeSeq
import net.liftweb.mapper.By

object InviteList {

       def whoToInvite(in: NodeSeq): NodeSeq = {
       
    		val projectId = S.param("id").open_!
       		val user = User.currentUser.open_! 
       		val listAlreadyInProject = Project.getProjectByIdAndByCurrentUser(projectId).users
       		val allUser = User.findAll()
       
       		<ul>{
                    val possibleInvite = allUser.filter(user => !(listAlreadyInProject.contains(user)))
                    
                    possibleInvite.map(user => <li>{ user.accountID.is }</li>)
        	}</ul>
       }

}