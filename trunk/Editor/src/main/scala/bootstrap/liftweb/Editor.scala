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

        // Encode string as an HTML identifier.
        // Must start with a letter.
        // Can include letters, digits, -, _, :, and .
        // We exclude : because of namespace issues.
        def idEncode(s: String) = {
          "K" + augmentString(s).map {
            case c if 'A' <= c && c <= 'Z' => c.toString
            case c if 'a' <= c && c <= 'z' => c.toString
            case c if '0' <= c && c <= '9' => c.toString
            // case c @ '-' => c.toString
            // case c @ ':' => c.toString
            case c @ '_' => "__"
            // case c @ '.' => c.toString
            case c => "_" + c.toInt + "_"
          }.mkString
        }


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


                val key = idEncode(filename + joker + index)
			 
			<lift:surround with={if(file){"editorFile"}else{"editorProject"}} at="content">	

			<script type="text/javascript">
			  var filename = '{filename}';
			  var key = '{key}';
	        </script>

			<div style="display: none;" id="userID" name="userID" >{code.model.User.currentUser.openOr(null: User).id.is}</div>

			<div id="save_form">
	        <form class="lift:form.ajax" name="save_form">
			<lift:SaveBuffer filename={S.param("path").openOr("").toString}>	    	
			<textarea style="display:none" id="buffer" name="buffer"/>
	      	<!--
	    	<input type="hidden" id="buffer" name="buffer"/>
	      	-->
	      	<input type="hidden" id={filename} name="filename" value={S.param("path").openOr("").toString}/>
	      	<input type="hidden" id="projectId" name="projectId" value={S.param("id").openOr("").toString}/>
	  		</lift:SaveBuffer>
	        </form>
	      	</div>





			<div id="main_div">
	      	<!-- left part -->
			<div class="left-slider" id="left_sidebar" value="true">
								<a class="left-handle" href="#">Content</a>
			                    <div class="tree lift:FileIn.cometFileList" style="display:inline-block">
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
			                    <div class="treeForm">
			                        <form class="lift:form.ajax">
			                            <div class="lift:FileIn.hiddenProjectId"></div>
			                            <input type="hidden" id="currentDir" name="currentDir" value=""/>
			                            <input class="lift:FileIn.newFile" id="fileIn"/><br/>
			                          <div class="filesRadio"><input type="radio" name="type" id="typefile" value="file" checked="ckecked" />
	      								<label for="typefile">File</label><input type="radio" name="type" id="typefolder" value="folder" />
	      								<label for="typefolder">Folder</label>
	      						</div>
	      								
			                           
			                            <div class="createSubmit"><input type="submit" value="Create"/></div>
			                        </form> 
			                    </div>
			</div>	

	       	<!-- middle part -->
	      	<div id="codearea">
	      		{
					if(file) {
						<textarea id={key} name={key} class="editor"></textarea>
						
						<div id="tabs">
							<ul>
								<li><a href="#tabs-1">Log</a></li>
								<li><a href="#tabs-2">Terminal</a></li>
							</ul>
							<div id="tabs-1">
								<div id="log">
								</div>
							</div>
							<div id="tabs-2">
								<iframe src="http://localhost:4200" style="width:100%; height:70px; margin:0 auto; border=0" frameborder="0"> 
								</iframe>
							</div>
						</div>				
						}
				}	
			</div>
	      	<!-- right part -->
			<div class="right-slider" id="right_sidebar" value="true">
				<a class="right-handle" href="#">Content</a>
	      		<div id="chat_content">
				<lift:comet type="InProjectViewer">
		                 <span name="list" id="inProject_out"></span>
		           </lift:comet>
	      		<lift:comet type="Chat" name={index+filename}>
	      		<h5 style="text-align:center">Chat</h5>
	      		<ul>
	      			<li>A message</li>
	      		</ul>
	      		</lift:comet>

	      		<div>
	      		<form class="lift:form.ajax">
	      			<input class="lift:ChatIn" id="chat_in" />
	      			<input type="hidden" name="filename" value={filename}/>
	      			<input type="hidden" name="name" value={User.currentUser.openOr(null).accountID.is}/>
	      			<input type="hidden" name="projectId" value={index}/>	
	      			<div class="chatButton"><input type="submit" value="Send"/></div>
				</form>
	      		</div>
	      		</div>
			</div>
			</div>


			<div id="box" class="dialog">
	      			<div style="text-align:center">
	      				<span id="txt">A nice file viewer</span><br />
	      				<button>OK</button>
	      			</div>
			</div>

			<lift:comet type="EnterProjectNotifier" name={index.toString}>
			           </lift:comet>

			    
			</lift:surround>
			
		}	
	}
	