package code
package snippet

import java.lang.reflect._
import java.io._
import java.util.GregorianCalendar

/**
 * Class used to compile the source file passed as parameters
 * @author: Lorenzo Baracchi <lorenzo.baracchi@member.fsf.org>	
 */
class Compiler(projectDirectory: String, optionList: scala.Array[String]) {

	// fields:
  	val runTime = Runtime.getRuntime 
  	var filesToCompile = ""
  	var time = ""
  	
  	/*
	 * Return the time of compilation.
	 */
	 def getTime() =
	 {
	 	time
	 }
  	
  	/*
  	 * Execute the given command from the terminal
  	 */
  	def copyFiles(src: String) =
  	{
  		val c = scala.Array("/bin/bash", "-c", "cd " + projectDirectory + "/src && curl -O " + src)
  		val pr = runTime.exec(c)
  		
		// wait for the end of the command execution
  		pr.waitFor()
  		
		// return the exit value   
  		val returnValue = pr.exitValue()
  		println(scala.Console.BLUE + "### COPY ###: " + returnValue + scala.Console.RESET)
  		returnValue
  	}

	/* 
	 * check if the given option could be recognized by the compiler
	 * @return true if it is recognized, false otherwise
	 */
	def checkOptionExists(option: String) = {
		println(option)

		option match{
			case "-verbose" => true
			case "-nowarn" => true
			case "-optimise" => true
			case  s:String if(s.startsWith("-classpath")) => true
			case  s:String if(s.startsWith("-sourcepath")) => true
			case  s:String if(s.startsWith("-bootclasspath")) => true
			case  s:String if(s.startsWith("-d")) => true
			case  s:String if(s.startsWith("-target")) => true
			case "-explaintypes" => true
			case _ => false
		}
	}

	/*
	 * Add a list of option to the command
	 * @return the command as a String
	 * @throws IllegalArgumentException if one of the option is not recognized
	 */
	def addOptionList(s: String) = {
		var newS=s
		for(opt <- optionList) {
			//println(opt)
			if(checkOptionExists(opt))
				newS=newS+" "+opt
			else
				throw new IllegalArgumentException("Option does not exists")
		}
		newS
	}
	
	/*
	 * Read the makefile and generates the list of all files to compile
	 */
	def readMakefile() 
	{
		val mkfile = scala.io.Source.fromFile(projectDirectory+"/.makefile").mkString
		var listFile = mkfile.split("\n")
		
//		for(q <- listFile)
//		{
//			println(scala.Console.YELLOW + "listFile: " + q + scala.Console.RESET)
//		}

//		println(scala.Console.YELLOW + "filesToCompile: " + filesToCompile + scala.Console.RESET)
		
		var lastDir = ""
		for(s <- listFile) 
		{
			if(s.endsWith(":"))
			{
				lastDir = s.substring(0, s.length()-1) + "/"
			}
			if(s.endsWith(".scala"))
			{
				filesToCompile = filesToCompile + " " + lastDir + s
			}
		}
	}
	 
	/*
	 * Send the given commad to the system which will execute it
	 * and save the result to the given log file.
	 */
  	def execute(cmd: String, log: String) = 
  	{
  		println(scala.Console.YELLOW + "CMD (exec): " + cmd + scala.Console.RESET)
  		println(scala.Console.YELLOW + "LOG (exec): " + log + scala.Console.RESET)
  		
  		// execute the command and redirect the output to a log (both stdout and stderr)
  		val command = scala.Array("/bin/bash", "-c", cmd + " &> " + log)
  		val pr = runTime.exec(command)
  		
		// wait for the end of the command execution
  		pr.waitFor()
  		
		// return the exit value   
  		val returnValue = pr.exitValue()
  		println(scala.Console.BLUE + "### EXECUTE ###: " + returnValue + scala.Console.RESET)
  		returnValue
  	}
  	
  	/*
  	 * As the name says: it compiles
  	 */
  	def compile () = 
  	{
  		println("[" + scala.Console.GREEN + "Compiling..." + scala.Console.RESET + "]")
  		var s = "scala-2.8.1.final/bin/scalac" 
		try{
			s = addOptionList(s)
	  		//for(file <- filesList) {
	  		//	s=s+" "+file
	  		//}
	  		
	  		// get files to be compiled from the makefile
	  		readMakefile()
	  		
			s = s + filesToCompile
	  		var gc = new GregorianCalendar();
	  		time = gc.getTime().toString().replace(" ", "_")
//	  		println(s + "log at log/log_compilation_" + time)
	  		
	  		var r = execute(s, projectDirectory + "/log/log_compilation_" + time)
//			var r = execute("pwd", projectDirectory + "/where.txt")
	  		
	  		r match
	  		{
	  			case 0 => println(scala.Console.GREEN + "Compilation successful!" 
	  			+ scala.Console.RESET)
	  			case 1 => println(scala.Console.RED + "Error: compilation failed" 
	  			+ scala.Console.RESET)
	  			case _ => println(scala.Console.RED_B + scala.Console.BLINK + "Unknown error..." +
	  			scala.Console.RESET)
	  		}
		}
		catch {
  			case e:IllegalArgumentException => println("Exception thrown: "+e.getMessage())
		}
  	}

}

/*
 * Just for testing purpose
 */
object Compiler {
	def main(args: scala.Array[String]){
		var lo = scala.Array("-verbose")
  		var c = new Compiler("project1", lo);
  		c.compile();
  	}
}
