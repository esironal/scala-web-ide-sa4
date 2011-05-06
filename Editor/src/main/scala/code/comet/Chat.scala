package code
package comet

import net.liftweb._
import http._
import util._
import Helpers._
import common._
import sitemap._
import Loc._
import mapper._
import js._
import JsCmds._
import JE._

import scala.xml.NodeSeq
import scala.collection.mutable.ArrayBuffer

import snippet._

/**
 * The screen real estate on the browser will be represented
 * by this component.  When the component changes on the server
 * the changes are automatically reflected in the browser.
 */
class Chat extends CometActor with CometListener {
	private var msgs: ArrayBuffer[String] = ArrayBuffer("Welcome") // private state

  /**
   * When the component is instantiated, register as
   * a listener with the ChatServer
   */
  def registerWith = ChatServer

  /**
   * The CometActor is an Actor, so it processes messages.
   * In this case, we're listening for Vector[String],
   * and when we get one, update our private state
   * and reRender() the component.  reRender() will
   * cause changes to be sent to the browser.
   */
  override def lowPriority = {
//    case v: Map[String,Vector[String]] => name match {
//		case Full(k) => msgs = v.getOrElse(k, Vector()); reRender()
 //   	case _ =>
   // }
  // case v:Map[String, ArrayBuffer[String]] => msgs = name.map(k => v(k)).openOr(ArrayBuffer());
   case v:Map[String, ArrayBuffer[String]] => msgs = v.getOrElse(name.openOr(""), ArrayBuffer()); reRender()
   case _ => msgs = ArrayBuffer("Something is WRONG!"); reRender()
  }

  /**
   * Put the messages in the li elements and clear
   * any elements that have the clearable class.
   */
  def render = {
  	"li *" #> msgs
  }
}
