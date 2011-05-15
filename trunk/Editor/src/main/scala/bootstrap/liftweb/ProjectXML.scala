package bootstrap.liftweb

import java.io.File
import scala.xml._
import code.model.Project

import net.liftweb._
import net.liftweb.mapper.Genders
import util._
import Helpers._

import common._
import http._
import sitemap._
import Loc._
import mapper._
import scala.xml.NodeSeq

object ProjectXML{

	var counter: Int = 1
	
	def projectHTML(myId: Long):Node = {
		
		val projBox: Box[Project] = Project.find(By(Project.id,  myId))
		val project : Project = projBox.openOr(null)

		val path: String = project.path
		return <div id="demo1" class="demo" ><ul>{dirHTML(path)}</ul></div>

	}

	def dirHTML(path: String):Node = {	
		var idT: String = "phtml_" + counter
		val list = new File(path).list
		val fileName = path.split("/")(path.split("/").length - 1)
		var result = <li id={idT}> 
						<a href="#">{fileName}</a>
		{counter = counter + 1
		if(list.length > 0){
					  <ul>
		
		{for (aFile <- list) yield
			{
			val temp = new File(path +"/"+  aFile)
			if(temp.isFile){
				//missing increasing counter
							<li id={idT} rel="file">
								<a href="#">{aFile}</a>
							</li> 

			}else{
				dirHTML(path +"/"+aFile)
			}
			}					
		}
		 </ul>
}
		}
		</li>
		

		return result
	}
}
