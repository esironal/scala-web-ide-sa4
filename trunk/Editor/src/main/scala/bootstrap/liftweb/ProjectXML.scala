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

class ProjectXML{


	def projectHTML(myId: Long):Node = {
		
		val projBox: Box[Project] = Project.find(By(Project.id,  myId))
		val project : Project = projBox.openOr(null)

		val path: String = project.path
		return <ul id="example" class="filetree">{dirHTML(path)}</ul>

	}

	def dirHTML(path: String):Node = {
		val list = new File(path).list
		val fileName = path.split("/")(path.split("/").length - 1)

		val result = <li>
		<span class="folder">{ fileName }</span>
		<ul>
		{for (aFile <- list) yield
			{
			val temp = new File(path +"/"+  aFile)
			if(temp.isFile){
				<li> <span class="file">{aFile}</span></li> 

			}else{
				dirHTML(path +"/"+aFile)
			}
			}						
		}
		</ul>
		</li>

		return result
	}
}
