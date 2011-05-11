package code
package snippet

import scala.xml._
import java.io._

/*
 * Run compiler and parse generated log.
 * @author: Cristian Bruseghini <cristian.bruseghini@gmail.com>	
 */
 
class RunCompiler(id: String, path: String, var options: Array[String])
{
	def run() = 
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
		scala.xml.XML.save(logFile + ".xml", <node>{log}</node>)
	}
}
