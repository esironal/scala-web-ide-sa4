package code
package comet

import net.liftweb._
import http._
import actor._
import scala.collection.mutable.ArrayBuffer
import scala.collection.immutable.Iterable

import model.FileChatMessage


/**
 * A singleton that provides chat features to all clients.
 * It's an Actor so it's thread safe because only one
 * message will be processed at once.
 */
object ChatServer extends LiftActor with ListenerManager {
  private var msgs = Map.empty[String, ArrayBuffer[String]] // private state

  val allMessages = FileChatMessage.findAll()
  
  for(m <- allMessages) {
  	val room = m.projectID.is + m.fileName.is
  	
  	msgs = msgs + (room->((msgs.getOrElse(room, ArrayBuffer()) += m.text.is)))
  }

  /**
   * When we update the listeners, what message do we send 
   * We send the msgs, which is an immutable data structure,
   * so it can be shared with lots of threads without any
   * danger or locking.
   */
  def createUpdate = msgs

  /**
   * process messages that are sent to the Actor.  In
   * this case, we're looking for Strings that are sent
   * to the ChatServer.  We append them to our Vector of
   * messages, and then update all the listeners.
   */
  override def lowPriority = {
	case (s: String, filename:String, projectId:String) => {

		val room = projectId + filename
    	msgs = msgs + (room->((msgs.getOrElse(room, ArrayBuffer()) += s)));
    	
    	updateListeners()
    }
  }
  
}
