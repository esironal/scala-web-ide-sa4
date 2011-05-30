package code
package lib

import net.liftweb.http._
import net.liftweb.http.rest._
import code.model._
import code.model.User._
import code.comet._

object RestCompiler extends RestHelper {
  serve {
    case Req("userId" :: userId :: "projectId" :: projectId, "xml", GetRequest) => 
      
      println("projectID: "+ projectId)
      val parameters = new Array[String](1)
      parameters(0) = "-verbose"
      var result = FileManager.compile(projectId(0), userId(0), parameters)
      
      <b>{result}</b>
  }
}

// User.currentUser.openOr(null: User).id.is