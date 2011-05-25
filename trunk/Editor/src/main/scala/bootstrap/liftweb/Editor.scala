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
import code.comet.FileManager


object Editor extends LiftView {

	LiftRules.useXhtmlMimeType = false

	val joker :String = "_-_" 

	def dispatch = {
		case filename:String => () => Full(newEditor().openOr(null))	
	}

	def newEditor(): Box[NodeSeq] = {
		val id = S.param("id");
		val path = S.param("path");	

					if(id.openOr("filenotfound") == "filenotfound"){
						errorPage("Specify a project id in the URL")
					} else {
						val myid = id.open_!
						
						if(path.openOr("notspecified") == "notspecified"){
							content("",  java.lang.Long.parseLong(myid), false)
						} else {
							
 						val mypath = path.open_!
						val projectBox: Box[Project] = Project.find(By(Project.id, java.lang.Long.parseLong(myid)))
						val project: Project = projectBox.openOr(null)
						if(project == null) {
							errorPage("The project you specified is not accessible or it doesn't exists")
						} else {
							try { 
				
								FileManager.openFile(project.path + "/" + mypath)
								content(mypath, project.id.is, true)
				
							} catch {
								case e: Exception => errorPage(e + "/" + project.path + "/" + mypath)
							}
						}
					}
				
					}
		
	}

	def errorPage(): NodeSeq = {
		<span>{"TODO"}</span>
	}

	def errorPage(s: String): NodeSeq = {
		<span>{s}</span>
	}

	def content(filename: String, index: Long, file: Boolean):NodeSeq = {	

			 
			<lift:surround with={if(file){"editorFile"}else{"editorProject"}} at="content">	

			<script type="text/javascript">
			  var filename = '{filename}';
			  var key = '{filename + joker + index}';
	        </script>

			<div id="save_form">
	        <form class="lift:form.ajax" name="save_form">
	        <lift:SaveBuffer filename={S.param("path").openOr("").toString}>
	    	<textarea style="display:none" id="buffer" name="buffer"/>
	      	<!--
	    	<input type="hidden" id="buffer" name="buffer"/>
	      	-->
	      	<input type="hidden" id={filename} name="filename" value={S.param("path").openOr("").toString}/>
	      	<input type="hidden" id="projectId" name="projectId" value={S.param("id").open_!.toString}/>
	  		</lift:SaveBuffer>
	        </form>
	      	</div>

			<input type="button" value="Save" onclick="javascript:saveBuffer()"/>




			<table id="main_table">
			<tr>
			<td id="left_sidebar" value="true">

			                    <div class="tree lift:FileIn.cometFileList">
			                        <span id="backlink">back</span>
			                        <img src="/filelist-template/img/current_folder.png"/>
			                        <span id="dirName">Current folder</span>
			                        <hr/>
			                        <div class="scrollable">
			                            <ul class="list">
			                                <li><a href="#">file name</a>-<a href="#">delete</a></li>
			                            </ul>
			                        </div>
			                    </div>
			                    <div>
			                        <form class="lift:form.ajax">
			                            <div class="lift:FileIn.hiddenProjectId"></div>
			                            <input type="hidden" id="currentDir" name="currentDir" value=""/>
			                            <input class="lift:FileIn.newFile" id="fileIn"/>
			                            <input type="radio" name="type" value="file" checked="ckecked" /> 
			                            File <input type="radio" name="type" value="folder" /> 
			                            Folder <br/>
			                            <input type="submit" value="Create"/>
			                        </form>
			                    </div>
					</td>			
			<td id="codearea">
			{
				if(file) {
					<textarea id={filename + joker + index} name={filename + joker + index} class="editor"></textarea>
				}
			}
			<div id="log">
			<div class='logDiv'>
				<p>LOG HERE</p>
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
			<button>OK</button></div>
			</div>

			</lift:surround>
		}	
	}
	
