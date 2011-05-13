package code
package snippet

import java.lang.reflect._
import java.io._

/*
 * Classed used to provide well formatted paramenteres to the Compiler
 * @author: Lorenzo Baracchi <lorenzo.baracchi@member.fsf.org>	
 */
class CompilerHelper(id: Int, var optionList: scala.Array[String]) 
{
	// fields:
	val runTime = Runtime.getRuntime 
	val projectId = "project_"+id
	
	/*
	 * Return location of the locally stored copy of the file.
	 */
	def getPath() = 
	{
		projectId
	}
	 
	/*
	 * @return the list of options	
	 */
	def getOptionList = optionList 
	 
	/*
	 * Set (as compiler option) the folder where to put bins
	 */	
	private def setBinDir() {
		optionList=optionList++scala.Array("-d "+projectId+"/bin")
	}
	 
	/*
	 * Set (as compiler option) the folder where to put source files
	 */	
	private def setSrcDir() {
		optionList=optionList++scala.Array("-d "+projectId+"/src")
	}
	
	/*
	 * Create the directories need to compile the project
	 * @return true if the process suceeded, false otherwise
	 */
	def createCompilerBox() = {
		setBinDir()
		//setSrcDir()
		// create a command to do that!
		var cmd = "mkdir -p "+projectId+" "+projectId+"/log"+" "+projectId+"/src"+" "+projectId+"/bin"
		val command= scala.Array("/bin/bash", "-c", cmd)
		
		val pr = runTime.exec(command)
  		
		// wait for the end of the command execution
  		pr.waitFor()
  		
		//get the exit value   
  		var exitStatus = pr.exitValue()
  		
  		if(exitStatus==0)
  			true
  		else
  			false
	}
	
	/*
	 * Create a sort of makefile	
	 */
	 def makeMakefile() = 
	 {	
	 	var cmd = "ls -1R " + projectId + " &> " + projectId + "/.makefile"
//		var cmd = "pwd &> " + projectId + "/.makefile"
	 	println(scala.Console.YELLOW + "CMD (mkfile): " + cmd + scala.Console.RESET)
	 	
	 	val c = scala.Array("/bin/bash", "-c", cmd)
  		val pr = runTime.exec(c)
  		
		// wait for the end of the command execution
  		pr.waitFor()
  		
		// return the exit value   
  		val returnValue = pr.exitValue()
  		println(scala.Console.RED + "### MAKEFILE ###: " + returnValue + scala.Console.RESET)
  		returnValue
	 }
	
	/*
	 * Destroy the directories used to compile the project
	 * @retrun true if the process suceeded, false otherwise
	 */
	def destroyCompilerBox() = {
		// create a command to do that!
		var cmd = "rm -rf "+projectId
		val command= scala.Array("/bin/bash", "-c", cmd)
		
		val pr = runTime.exec(command)
  		
		// wait for the end of the command execution
  		pr.waitFor()
  		
		//get the exit value   
  		var exitStatus = pr.exitValue()
  		
  		if(exitStatus==0)
  			true
  		else
  			false
	}
}

/*
 * Just for testing
 */
 object CompilerHelper  {
	def main(args: scala.Array[String]){
  		var ch = new CompilerHelper(1, scala.Array("-verbose"));
  		var o=ch.createCompilerBox();
  		println("folders created: "+o)
  		var 0=ch.makeMakefile()
  		println("makefile: "+o)
  	/*	var t0=System.currentTimeMillis();
  		var t1=t0
          do{
              t1=System.currentTimeMillis();
          }while (t1-t0<5000);
		o=ch.destroyCompilerBox();
  		println("folders deleted: "+o) */
  	}
}
