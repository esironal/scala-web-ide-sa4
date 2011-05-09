package code
package snippet

/**
 * Run the compilation and parsing of generated log.
 * @autor: Cristian Bruseghini <cristian.bruseghini@gmail.com>	
 */
 
class RunCompiler(id: String, path: String, options: Array[String])
{
	def run() = 
	{		
		// create a new CompilerHelper in order to create logs
		val compileHelper = new CompilerHelper(Integer.parseInt(id), options)
		
		// create new project_folder with the specific id
		compileHelper.createCompilerBox()
		
		val src = compileHelper.getPath()
		
		// create a new Compiler to which pass the path of the file to be compiled and
		// a list of options
		val compiler = new Compiler(src, options)
		
		// copy the file in projectId/src
		compiler.copyFiles(src)		
		
		// compile the file located at a specific path
		compiler.compile()
		
		// parse the log
		LogParser.getXMLlog(compileHelper.getLog())
	}
}
