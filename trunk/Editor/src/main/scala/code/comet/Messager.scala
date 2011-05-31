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
import scala.xml._

	

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
			
			case Compile(path: String, userId: Long, options: Array[String]) => {
				println("Compile(path: "+path+", userId: "+userId+", options:"+options+")")
				sender ! FileManager.compile(path, userId, options)
			}
			
			case ('compiled, id: Int, path: String) => {
				println("Compiled(Id: "+id+", path: "+path+")")
				var zip_path = FileManager.getCompiledZip(path, id)
				println("before")
				var logPath = FileManager.getLogPath(id)
				var log: NodeSeq = FileManager.getLog(logPath) 
				println("LOG: "+log)
				FileManager.delete("http://localhost:8081/compiler/link/"+id)
				sender ! "Notification received: Compiled(Id: "+id+", path: "+path+")"
				
				val rest = select(scala.actors.remote.Node("localhost", 8083), 'rest)
				rest ! log
				FileManager.deleteFile(logPath+"../../","project_"+id)
				
				
			}
			
			
			}
						
		} 
				
	}
}

	