import java.lang.reflect._
import java.io._
import java.util.GregorianCalendar

/*
 * Class used to compile the source file passed as parameters
 * @autor: Lorenzo Baracchi <lorenzo.baracchi@member.fsf.org>	
 */
class Compiler(filesList: scala.Array[String]) {

	

  	val runTime = Runtime.getRuntime 
	
	// send the given commad to the system which will execute it
	// and save the result to the given log file
  	def exec (cmd: String, log: String) = {
  		//execute the command and redirect output to a log (both stdout and stderr)
  		val command= scala.Array("/bin/bash", "-c", cmd+" &> "+log)

  		val pr = runTime.exec(command)
  		
  		/*var output=""
  		//get the output stream if any
  		val resultBuffer = new BufferedReader(new InputStreamReader(pr.getInputStream))
	    	var line : String = null
		do {
			line = resultBuffer.readLine
			if (line != null) {
				output=output+line
			}
		} while (line != null) 
  		
  		//wait for the process to finish
  		
  		runTime.exec("echo "+output+" > "+log);*/
  		pr.waitFor()
  		//get the error stream if any
  		/*val errBuffer = new BufferedReader(new InputStreamReader(pr.getErrorStream))
	    	var line = null
		do {
			line = errBuffer.readLine
			if (line != null) {
				println (line)
			}
		} while (line != null) */
		//return the exit value   
  		pr.exitValue()
  	}
  	
  	// As the name say: it compiles
  	def compile () = {
  		println("Please wait, Compiling...")
  		var s = "scalac -verbose" 
  		for(file <- filesList) {
  			s=s+" "+file
  		}
  		var gc= new GregorianCalendar();
  		var time=gc.getTime().toString()
  		time=time.replace(" ", "_")
  		println(s+ "log at log/log_compilation_"+time)
  		var r= exec(s, "log/log_compilation_"+time)
  		r match{
  		  	case 0 => println("Copilation successful "+r)
  			case 1 => println("Error occurred "+r)
  			case _ => println("Unknown error "+r)
  		}
  	}

}

/*
 * For testing purpose
 */
object Compiler {
	def main(args: scala.Array[String]){
  		var c = new Compiler(scala.Array("HelloWorld.scala"));
  		c.compile();
  	}
}
