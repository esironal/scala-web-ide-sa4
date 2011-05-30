package code
package comet

import net.liftweb._
import http._
import actor._

import scala.collection.mutable.Set
import scala.collection.mutable.HashMap

import scala.actors.remote.FreshNameCreator

import scala.util.Random
import scala.xml.{ Node, NodeSeq, XML }

import code.model._

object UserInProjectDispatcher extends LiftActor with ListenerManager {

	private var userProject: HashMap[Long, Set[Long]] = new HashMap[Long, Set[Long]]

	def createUpdate = userProject
	
	override def lowPriority = {
		case ('Enter, projectId: Long, userId: Long) => {
			add(projectId, userId)
			updateListeners()
		}
		case ('Leave, projectId: Long, userId: Long) => {
			remove(projectId, userId)
			updateListeners()
		}
	}
	
	def add(projectId: Long, userId: Long): Unit = {
		if(! userProject.contains(projectId)) {
			val userSet: Set[Long] = Set()
			userSet.add(userId)
			
			userProject.put(projectId, userSet)
		} else {
			val userSet = userProject.get(projectId)
			
			userSet match {
				case Some(_) => {		
					(userSet.get).add(userId)
				}
				case None => println("Debug Add...")
			}
		}
	}
	
	def remove(projectId: Long, userId: Long): Unit = {
		if(userProject.contains(projectId)) {
			val userSet = userProject.get(projectId)
			
			userSet match {
				case Some(_) => {		
					(userSet.get).remove(userId)
				}
				case None => println("Debug Leave...")
			}
		}
	}
}