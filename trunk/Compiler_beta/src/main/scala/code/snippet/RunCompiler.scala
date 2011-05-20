package code
package snippet

import scala.xml._
import java.io._
import scala.actors.Actor._
import scala.actors.Actor
import scala.actors.remote.RemoteActor._
import scala.actors.remote._

import code.comet._

/*
 * Run compiler and parse generated log.
 * @author: Cristian Bruseghini <cristian.bruseghini@gmail.com>	
 */
 
class RunCompiler(id: String, path: String, var options: Array[String]) extends Actor
{
	classLoader = getClass.getClassLoader
	
	def act
	{		
		// create a new CompilerHelper in order to create logs
		val compileHelper = new CompilerHelper(Integer.parseInt(id), options)
		
		// create new project_folder with the specific id
		compileHelper.createCompilerBox()
		
		// update the options' list
		options = compileHelper.getOptionList
		
		val src = compileHelper.getPath()
		
		// create a new Compiler to which pass the path of the file to be compiled and
		// a list of options
		val compiler = new Compiler(src, options)
		
		// copy the file in projectId/src
		compiler.copyFiles(path)		
		
		// create makefile
		compileHelper.makeMakefile()
		
		// compile the file located at a specific path
		compiler.compile()
		
		// create log.xml
		val logFile = src + "/log/log_compilation_" + compiler.getTime()
		val log = LogParser.getXMLlog(logFile)
		scala.xml.XML.save(logFile + ".xml", log)
		
		// zip binary files and log
		compileHelper.zipBinAndLog()

		// send case class Compiled to notify end of compilation
		println(scala.Console.BLUE + "### SEND NOTIFICATION ###" + scala.Console.RESET)
		// val compiled = new Compiled(Integer.parseInt(id), path)
		val server = select(scala.actors.remote.Node("localhost", 8082),'server)
		// val result = server !? compiled
		val result = server !? (('compiled, Integer.parseInt(id), path))
		println(scala.Console.BLUE + "### RECEIVED NOTIFICATION ###: " + result + scala.Console.RESET)
	}
}
