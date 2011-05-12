package bootstrap.liftweb

import java.io.File
import scala.xml._
import code.model._


class ProjectXML{


	def projectHTML(myId: String):Node = {
		val projBox: Box[Project] = Project.find(By(Project.id,  myId))
		val project : Project = project.openOr(null)

		val path: String = project.path.id
		return finalHTML(path)

	}

	def finalHTML(rootPath: String):Node = {<ul id="example" class="filetree">{dirHTML(rootPath)}</ul>}

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
