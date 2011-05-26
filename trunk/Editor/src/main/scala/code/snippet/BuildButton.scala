package code
package snippet

import net.liftweb._
import http._
import common._
import js._
import JsCmds._
import JE._

import S._
import util.Helpers._
import scala.xml._


import net.liftweb.http.ListenerManager
import net.liftweb.actor.LiftActor
import java.io._
import java.util.zip
import java.net._
import scala.io.Source
import code.model.User
import net.liftweb.http.S
import code.model.Project
import scala.collection.mutable.HashMap
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.OutputStream
import java.io.PrintStream
import java.io.Reader
import code.model.User._

object BuildButton  {
  
//  private var result: Box[NodeSeq] = Empty
//
//  val dispatch: DispatchIt = { case _ => compile }
//  
//  
//  def compile = {
//    def processEntry = {
//      val parameters = new Array[String](1)
//      parameters(0) = "-verbose"
//      println("@@@@@@ ziugan prima ")
//      S.notice(code.comet.FileManager.compile(S.param("id")+"",User.currentUser.openOr(null: User).id.is,parameters))
//      //println("@@@@@@ ziugan dopo "+result)
//      result = Full("ciao")
//    }
//
//    "#log" #> result.openOr(<p>{SHtml.onSubmitUnit(processEntry _)}</p>)
//  }
  
  
  
  def render = {
    
   var result = ""
     
   
     
	   def process() = {
     
     val parameters = new Array[String](1)
      parameters(0) = "-verbose"
      println("@@@@@@ ziugan prima ")
      result = code.comet.FileManager.compile(S.param("id")+"",User.currentUser.openOr(null: User).id.is,parameters)
      println("@@@@@@ " + S.param("id") + " $$$$$$$$$$ " + User.currentUser.openOr(null: User).id.is )
        
        
        
   }
     
     
   "type=submit" #> SHtml.onSubmitUnit(process)
   
   
    
  }
  
    
    
    
    
}
  
 
