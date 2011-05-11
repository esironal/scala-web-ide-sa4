package code
package snippet

import scala.xml._
import java.io._

/*
 * Run compiler and parse generated log.
 * @autor: Cristian Bruseghini <cristian.bruseghini@gmail.com>	
 */
 
class RunCompiler(id: String, path: String,var options: Array[String])
{
	def run() = 
	{		
		// create a new CompilerHelper in order to create logs
		val compileHelper = new CompilerHelper(Integer.parseInt(id), options)
		
		// create new project_folder with the specific id
		compileHelper.createCompilerBox()
		
		//update the option's list
		options=compileHelper.getOptionList
		
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
		
		// parse the log
		var log = LogParser.getXMLlog(compileHelper.getLog())
		
		// create log.xml
//		var file = "log/log_compilation_" + compiler.getTime() + ".xml"
//		val fw = new FileWriter(file)
		
//		scala.xml.XML.save(file, <node>{log}</node>)
	}
}
