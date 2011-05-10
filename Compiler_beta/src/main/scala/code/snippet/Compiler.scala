package code
package snippet

import java.lang.reflect._
import java.io._
import java.util.GregorianCalendar

/**
 * Class used to compile the source file passed as parameters
 * @autor: Lorenzo Baracchi <lorenzo.baracchi@member.fsf.org>	
 */
class Compiler(sourcesDirectory: String, optionList: scala.Array[String]) {

	

  	val runTime = Runtime.getRuntime 
	
	/**
	 * send the given commad to the system which will execute it
	 * and save the result to the given log file
	 */
  	def exec (cmd: String, log: String) = {
  		//execute the command and redirect the output to a log (both stdout and stderr)
  		val command= scala.Array("/bin/bash", "-c", cmd+" &> "+log)
  		val pr = runTime.exec(command)
  		
		// wait for the end of the command execution
  		pr.waitFor()
  		
		//return the exit value   
  		pr.exitValue()
  	}
  	
  	/*
  	 * Execute the given command from the terminal
  	 */
  	def copyFiles(src: String) =
  	{
  		val c = scala.Array("/bin/bash", "-c curl -O " + src)
  		val exec = runTime.exec(c)
  		
		// wait for the end of the command execution
  		exec.waitFor()
  		
		//return the exit value   
  		exec.exitValue()
  	}

	/** 
	 * check if the given option could be recognized by the compiler
	 * @return true if it is recognized, false otherwise
	 */
	def checkOptionExists (option: String) = {
		var op=option.substring(option.indexOf(" ")+1)
		op match {
			case "-verbose" => true
			case "-nowarn" => true
			case "-optimise" => true
			case "-classpath" => true
			case "-sourcepath" => true
			case "-bootclasspath" => true
			case "-d" => true
			case "-target" => true
			case "-explaintypes" => true
			case _ => false
		}
	}

	/**
	 * Add a list of option to the command
	 * @return the command as a String
	 * @throws IllegalArgumentException if one of the option is not recognized
	 */
	def addOptionList (s: String) = {
		var newS=s
		for(opt <- optionList) {
			if(checkOptionExists(opt))
				newS=newS+" "+opt
			else
				throw new IllegalArgumentException("Option does not exists")
		}
		newS
	}
  	
  	/** 
  	 * As the name says: it compiles
  	 */
  	def compile () = {
  		println("Please wait, Compiling...")
  		var s = "scalac " 
		try{
			s=addOptionList(s)
	  		//for(file <- filesList) {
	  		//	s=s+" "+file
	  		//}
			s=s+" "+sourcesDirectory+"/*"
	  		var gc= new GregorianCalendar();
	  		var time=gc.getTime().toString()
	  		time=time.replace(" ", "_")
	  		println(s+ " log at log/log_compilation_"+time)
	  		var r= exec(s, "/Applications/lift-lift_23_sbt-68e7db1/lift_basic/src/main/scala/code/snippet/log/log_compilation_"+time)
	  		r match{
	  		  	case 0 => println("Copilation successful "+r)
	  			case 1 => println("Error occurred "+r)
	  			case _ => println("Unknown error "+r)
	  		}
		}
		catch {
  			case e:IllegalArgumentException => println("Error occurred: "+e.getMessage())
		}
  	}

}

/*
 * Just for testing purpose
 */
object Compiler {
	def main(args: scala.Array[String]){
		var lo = scala.Array("-verbose")
  		var c = new Compiler("sources", lo);
  		c.compile();
  	}
}
