package code
package comet

import scala.actors.Actor._
import scala.actors.Actor
import scala.actors.remote.RemoteActor._
import scala.actors.remote._
import comet._

object Client {

	def main(args: Array[String]){
	println("Client started")
	if(args.length != 2){
		println("Usage : [serverIpAddress] [serverPort]")
	}else{ 
		val server = args(0)
		val port = args(1).toInt
		val m = (Messager)
		val save = new Save("mypath")
		m ! save
		
	}
}


}


