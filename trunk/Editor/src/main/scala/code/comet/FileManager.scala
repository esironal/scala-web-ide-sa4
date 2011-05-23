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
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.OutputStream
import java.io.PrintStream
import java.io.Reader

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
    	def delete(file: File): Unit = {
    	          if(file.isDirectory) {
    	            file.listFiles.foreach(delete(_))
    	          }
    	          file.delete
    	        }
    	        
    	    	delete(new File(filePath))
    }

    def openFile(filePath: String): String = {
    	Source.fromFile(new File(filePath)).getLines.mkString("\n")
    }
    
    // creates a zip file of the file on the given projectPath and returns its path
    def zipFile(path: String, projectName:String, userId: Long): String = {
    	
    	

    	//val a = Runtime.getRuntime.exec("wget -P,  --directory-prefix=PREFIX  http://www.dreamshade.ch/immagini/facebook/media")
    	
    	
    	
    	
    	val c = scala.Array("/bin/bash","-c", "cd "+path+" && zip -r ../compiled_projects/user_"+userId+"/"+projectName+".zip "+projectName)
    	// later, for compiled projects use: ../compiled_projects/user_"+userId+"/"+projectName+".zip"
    	
    	
  		val exec = Runtime.getRuntime.exec(c)
  		
		// wait for the end of the command execution
  		exec.waitFor()
  		
		//return the exit value   
  		val v = exec.exitValue()
  		println("zipFile: exec.exitValue() = " + v)
  		if(v == 0) {
  			return path+projectName+".zip"
  		}
    	return "failed"
	}
    
    // usually called by Messager after a compile request by the editor
    // this method creates a zip file, and sends its path (and options and  so on...)
    // through a JSON object
    // if the operation failed, a string with "failed" will be sent returned
    // and no JSON will be sent
    def compile(projectName: String, userId: Long, options: Array[String]): String = {
    	var zip = zipFile("./src/main/webapp/projects/",projectName, userId)
    	if(!zip.equals("failed")) {
    		var json = createJson(userId, zip, options)
    		put(json, "http://localhost:8081/compiler/link")
    		println("compiling...")
    		return "ok"
    	}
    	println("failed")
    	return "failed"
    }
    
    // creates a string ready to be used as JSON object
    def createJson(userId: Long, path: String, options: Array[String]): String = {
    	var optstr = ""
    	for(opt <- options){
    		optstr = optstr +"\"" +opt+ "\"" +", "
    	}
    	optstr = optstr.substring(0,optstr.length - 3) 
    	return "{\"id\":\""+userId+"\", \"path\":\""+path+"\", \"options\": ["+optstr+"\"]}"
    }

    // the HTTP PUT request method!
    def put(json: String, link: String) = {
    	val url: URL = new URL(link)
    	val httpCon: HttpURLConnection = (url.openConnection).asInstanceOf[HttpURLConnection]
    	httpCon.setDoOutput(true)
    	httpCon.setRequestMethod("PUT")
    	httpCon.setRequestProperty("Content-Type","application/json")
    	
    	val out: OutputStreamWriter = new OutputStreamWriter(httpCon.getOutputStream)
    	out.write(json)
    	out.close
    	println(httpCon.getResponseMessage)
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