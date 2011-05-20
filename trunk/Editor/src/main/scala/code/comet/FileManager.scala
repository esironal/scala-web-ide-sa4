package code.comet
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


object FileManager extends LiftActor with ListenerManager {

    def fileList(dirPath:String): List[String] = {
        (new File(dirPath).listFiles.map(_.getPath)).toList
    }

    def newFile(projectPath:String, name: String) = {
    	new File(projectPath, name).createNewFile
    }

    def saveFile(filePath: String, content: String) = {
        val writer = new PrintWriter(new File(filePath))
        try { writer.print(content) } finally { writer.close() }
    }

    def deleteFile(filePath: String) = {
    	new File(filePath).delete
    }

    def openFile(filePath: String): String = {
    	Source.fromFile(new File(filePath)).getLines.mkString("\n")
    }
    
    // creates a zip file of the file on the given projectPath and returns its path
    def zipFile(projectPath: String, userId: Long): String = {
    	val projArr = projectPath.split("/")
    	val i: Int = projArr.length - 1
    	val fileName = projArr(i)
    	val newProj = fileName+"_"+userId
    	val c = scala.Array(projectPath+"/..", "zip -r "+newProj+".zip "+fileName)
  		val exec = Runtime.getRuntime.exec(c)
  		
		// wait for the end of the command execution
  		exec.waitFor()
  		
		//return the exit value   
  		if(exec.exitValue() == 0) {
  			return projectPath+"/../"+newProj+".zip"
  		}
    	return "failed"
	}
    
    // usually called by Messager after a compile request by the editor
    // this method creates a zip file, and sends its path (and options and  so on...)
    // through a JSON object
    // if the operation failed, a string with "failed" will be sent returned
    // and no JSON will be sent
    def compile(projectPath: String, userId: Long, options: Array[String]): String = {
    	var zip = zipFile(projectPath, userId)
    	if(!zip.equals("failed")) {
    		var json = createJson(userId, zip, options)
    		put(json, "http://localhost:8081/compiler/link")
    		return "compiling..."
    	}
    	return "failed"
    }
    
    // creates a string ready to be used as JSON object
    def createJson(userId: Long, path: String, options: Array[String]): String = {
    	var optstr = ""
    	for(opt <- options){
    		optstr = optstr +"\"" +opt+ "\"" +", "
    	}
    	optstr = optstr.substring(0,optstr.length - 3) 
    	return "{\"id\":\""+userId+"\"; \"path\":\""+path+"\"; \"options\": ["+optstr+"]}"
    }

    // the HTTP PUT request method!
    def put(json: String, link: String) = {
    	val url: URL = new URL(link);
    	val httpCon: HttpURLConnection = (url.openConnection).asInstanceOf[HttpURLConnection];
    	httpCon.setDoOutput(true);
    	httpCon.setRequestMethod("PUT");
    	val out: OutputStreamWriter = new OutputStreamWriter(httpCon.getOutputStream);
    	out.write("Resource content");
    	out.close;
    }

    val dirMap = new HashMap[String,String]
    def createUpdate = dirMap

    override def lowPriority = {
        case ('new, projectPath: String, s: String) => newFile(projectPath, s); updateListeners()
        case ('delete, filePath: String) => deleteFile(filePath); updateListeners()
        case ('chdir, formId: String, filePath: String) => dirMap += (formId -> filePath); updateListeners()
        case _ => println("NOTHING")
    }
}
