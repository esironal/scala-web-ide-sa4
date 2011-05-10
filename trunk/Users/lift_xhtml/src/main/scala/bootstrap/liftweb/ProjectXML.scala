package bootstrap.liftweb

import java.io.File
import scala.xml._

class ProjectXML{

	def dirHTML(path: String):Node = {
		val list = new File(path).list
		val fileName = path.split("/")(path.split("/").length - 1)
		
		val result = <li><span class="folder">{ fileName }</span>
						<ul>
							{for (aFile <- list) yield
								<li> {
								val temp = new File(path +"/"+  aFile)
								if(!temp.isDirectory){
									<span class="file">{aFile}</span>
								}else{
									dirHTML(path +"/"+aFile)
								}
								}</li> 
							}
						</ul>
		</li>
		
		return result
}
}
