package code
package comet


import net.liftweb._
import http._
import actor._
import scala.actors.Actor._
import scala.actors.Actor
import scala.actors.remote.RemoteActor._
import scala.actors.remote._
import code.lib._



	

class Messager(port: Int) extends Actor {
			
	
	RemoteActor.classLoader = getClass().getClassLoader()

	def act = {
						
		alive(port)
						
		register('server, self)
						
		loop {
							
			react {
				
			case Save(path: String) => {
				println("File System: saved "+path+" (actually this is fake. it's just an example, we did not save it yet)")
				sender ! "File System: saved "+path+" (actually this is fake. it's just an example, we did not save it yet)"
			}
			
			case Compiled(id: Int, path: String) => {
				println("File System: compiled "+path+" with id " +id+ " (actually this is fake. it's just an example, we did not implement it yet)")
				sender ! "File System: compiled "+path+" with id " +id+ " (actually this is fake. it's just an example, we did not implement it yet)"
			}
							
			}
						
		} 
				
	}
}

	