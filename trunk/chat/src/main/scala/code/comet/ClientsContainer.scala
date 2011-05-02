package code
package comet

import net.liftweb._
import http._
import actor._
import scala.actors._
import js._
import JsCmds._
import JE._


case class SubscribeClient(client:Chat)
case class UnsubClient(client:Chat)
case object Message
 
object ClientsContainer extends Actor {
  
  	private var clients: List[Chat] = List()
  
  	def act = loop {		react { 
			case SubscribeClient(client) => clients ::= client
			case UnsubClient(client) => clients.filterNot(_ == client)
			case Message => SetHtml("test", <b>Got message</b>); clients.foreach(_ ! Message)		}	}
	
	def getClients():List[Chat] = {
		return clients	
	}
}
