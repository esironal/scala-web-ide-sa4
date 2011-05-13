package bootstrap.liftweb

import net.liftweb._
import util._
import Helpers._

import common._
import http._
import sitemap._
import Loc._
import mapper._

import js._
import JsCmds._
import JE._

import scala.xml.NodeSeq

import code.model._
import code.comet.ChatServer
import scala.collection.immutable._
import code.model.Project


object Editor extends LiftView {
  	def dispatch = {
  		case filename:String => () => Full(newEditor(filename))	
  	}

	def newEditor(fileName:String): NodeSeq = {
		
		val s: Array[String] = fileName.trim.split(":")
		
		if(s.length == 2) {
				var projectBox: Box[Project] = Project.find(By(Project.id, java.lang.Long.parseLong(s(0))))
				var project: Project = projectBox.openOr(null)

				if(project == null) {
					errorPage()
				} else {
					content(fileName, project.id.is)
				}
		} else if (s.length == 1) {
			var projectBox: Box[Project] = Project.find(By(Project.id, java.lang.Long.parseLong(fileName)))
			var project: Project = projectBox.openOr(null)

			if(project == null) {
				errorPage()
			} else {
				content(fileName, project.id.is)
			}
		} else { 
			errorPage()
		}
	}
	
	def errorPage(): NodeSeq = {
		<span>{"TODO"}</span>
	}
  	
  	def content(filename: String, index: Long):NodeSeq = {	
	
  	 <lift:surround with="default" at="content">
		<!--<hr>-->
		<table id="main_table">
			<tr>
				<td id="left_sidebar" value="true">
				{ProjectXML.projectHTML(index)}
				</td>			
				<td id="codearea">
					<textarea id={filename} name={filename} class="editor"></textarea>
					<div id="log">
						<div class='logDiv'>

						<table class='logTable' cellspacing='0'>

						<tr class='logTrErrorFirst'>

						<td class='logTd1'>17</td><td class='logTd2'><a href='http://NetBeansProjects/SA4Game/src/sa4game/Client.scala#17'>NetBeansProjects/SA4Game/src/sa4game/Client.scala:17</a></td>

						</tr>

						<tr class='logTrErrorSecond'>

						<td class='logTd1'></td><td class='logTd2'>Error - not found: type HashMap</td>

						</tr>

						<tr class='logTrErrorSecond'>

						<td class='logTd1'></td><td class='logTd2 logTdFixed'>  var games = new <u>HashMap</u>[String,Array[Array[Array[Character]]]]</td>

						</tr>

						<tr class='logTrErrorFirst'>

						<td class='logTd1'>12345</td><td class='logTd2'><a href='http://NetBeansProjects/SA4Game/src/sa4game/Client.scala#12345'>NetBeansProjects/SA4Game/src/sa4game/Client.scala:12345</a></td>

						</tr>

						<tr class='logTrErrorSecond'>

						<td class='logTd1'></td><td class='logTd2'>Error - not found: type Random</td>

						</tr>

						<tr class='logTrErrorSecond'>

						<td class='logTd1'></td><td class='logTd2 logTdFixed'>    val rnd = new <u>Random</u></td>

						</tr>

						<tr class='logTrFinalError'>

						<td class='logTd1'></td><td class='logTd2'>2 errors found</td>

						</tr>

						<tr class='logTrFinalCompileFailure'>

						<td class='logTd1'></td><td class='logTd2'>COMPILATION FAILED</td>

						</tr>

						</table>

						</div>
						
					</div>
				</td>
					<td id="right_sidebar" value="true">
					<div id="chat_content">
					<lift:comet type="Chat" name={filename}>
				        <h5 style="text-align:center">Chat</h5>
				        <ul>
				          <li>A message</li>
				        </ul>
				     </lift:comet>
				      
					  <div>
				        <form class="lift:ChatIn?form=post">
				          <input id="chat_in" />
					      <input type="hidden" name="filename" value={filename}/>
					      <input type="hidden" name="name" value="Jack"/>
				          <input type="submit" value="Send"/>
				        </form>
				      </div>
					</div>
					</td>
				</tr>
			</table>
			<div id="box" class="dialog">
			<div style="text-align:center"><span id="txt">A nice file viewer</span><br />
			<button onclick="hm('box');okSelected()">OK</button></div>
			</div>
      </lift:surround>
  	}	
}