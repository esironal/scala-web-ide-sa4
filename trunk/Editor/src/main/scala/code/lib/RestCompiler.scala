package code
package lib

import net.liftweb.http._
import net.liftweb.http.rest._

import net.liftweb._
import http._
import actor._
import scala.actors.Actor._
import scala.actors.Actor
import scala.actors.remote.RemoteActor._
import scala.actors.remote._
import code.model._
import code.model.User._
import code.comet._
import scala.xml._

object RestCompiler extends RestHelper {
  serve {
    case Req("userId" :: userId :: "projectId" :: projectId, "xml", GetRequest) => 
      
      println("projectID: "+ userId)
      val parameters = new Array[String](1)
      parameters(0) = "-verbose"
      var myLog: NodeSeq = <b></b>
      var ready = false
      
     class RestMessager(port: Int) extends Actor {
			
	
    	  RemoteActor.classLoader = getClass().getClassLoader()

    	  def act = {
						
    		  alive(port)
    		  register('rest, self)
						
    		  loop {
							
    			  react {
    			    case log:NodeSeq => {
    			      myLog = log
    			      println("loglog: "+ log)
    			      println("AFTER EDITOR CALL")
    			      ready = true
    			    }
    			  }
    		  }
    	  }
      }
      
      val messager = new RestMessager(8083)
      messager.start
    			 
      
      
      
      
      
      var result = FileManager.compile(projectId(0), Integer.parseInt(userId), parameters)
      
      while(!ready) {
    	  println("waiting...")
      }
      
      
      
      <b>{myLog}</b>
  }
}

// User.currentUser.openOr(null: User).id.is