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


object Messager extends LiftActor with ListenerManager {

	
	
	def createUpdate = ""
	
	 override def lowPriority = {
					case s: String => {
						println("ciabatta "+s)
						
    		updateListeners()
					}
			case Save(path: String) => {
				println("saved "+path)
				
    		updateListeners()
			}
		}
}

	