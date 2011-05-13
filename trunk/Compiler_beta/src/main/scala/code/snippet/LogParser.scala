package code
package snippet

import java.lang.String
import scala.xml._
import java.io._

object LogParser{
  def parseLog(logFile: String): String =
  {
    var string = ""
    try
    {
      // Set up a file reader to read one character at a time
      val input = new FileReader(logFile)

      // Set up a BufferedReader Read a line at time
      val bufRead = new BufferedReader(input)

      var line = bufRead.readLine()
      string = ""

      // Read through file one line at time
      while (line != null)
      {
        string = string + "\n" + line
        line = bufRead.readLine()
      }

//      println(string)

      bufRead.close()
    }
    catch
    {
      case e: IOException => println("LogParser - Exception thrown: " + e.getMessage())
    }

    var amount = 0
    var preLogArray = string.split("\n")
    var logArray:scala.collection.mutable.ArrayBuffer[String] = new scala.collection.mutable.ArrayBuffer;
    var length = 0
    for (i <- 0 until preLogArray.length)
    {
      if(!(preLogArray(i).length < 2 || preLogArray(i).startsWith("[") && preLogArray(i).endsWith("]")))
      {
        logArray += preLogArray(i)
        length = length + 1
      }
    }
    val logTd1 = "logTd1"
    val logTd2 = "logTd2"
    val logTdFixed = "logTdFixed"
    val logTrErrorFirst = "logTrErrorFirst"
    val logTrErrorSecond = "logTrErrorSecond"
    val logTrWarningFirst = "logTrWarningFirst"
    val logTrWarningSecond = "logTrWarningSecond"
    val logTrFinalError = "logTrFinalError"
    val logTrFinalWarning = "logTrFinalWarning"
    val logTrFinalCompileSuccess = "logTrFinalCompileSuccess"
    val logTrFinalCompileWarning = "logTrFinalCompileWarning"
    val logTrFinalCompileFailure = "logTrFinalCompileFailure"
    var logTrFinalCompile = logTrFinalCompileSuccess
    var logTrFinal=""
    var logTrFirst = ""
    var logTrSecond = ""
    var log = ""
    if(length > 0)
    {
      if(logArray(length-1).endsWith("error found") || logArray(length-1).endsWith("errors found"))
      {
        logTrFinal = logTrFinalError
        logTrFinalCompile = logTrFinalCompileFailure
        logTrFirst = logTrErrorFirst
        logTrSecond = logTrErrorSecond
      }
      else if(logArray(length-1).endsWith("warning found") || logArray(length-1).endsWith("warnings found"))
      {
        logTrFinal = logTrFinalWarning
        logTrFinalCompile = logTrFinalCompileWarning
        logTrFirst = logTrWarningFirst
        logTrSecond = logTrWarningSecond
      }
    }
    if(length > 1)
    {
      for (i <- 0 until length)
      {
        if(i == length-1)
        {
          logArray(i) = "\n<tr class='" + logTrFinal + "'>\n<td class='" + logTd1 +
          "'></td><td class='" + logTd2 + "'>" + logArray(i) + "</td>\n</tr>"
        }
        else
        {
          if(logArray(i).startsWith("warning: "))
          {
            logArray(i) = logArray(i).replaceFirst("warning", "Warning")
            logArray(i) = "\n<tr class='" + logTrWarningFirst + "'>\n<td class='" + logTd1 +
            "'></td><td class='" + logTd2 + "'>" + logArray(i) + "</td>\n</tr>"
          }
          else
          {
            if(i % 3 == 0)
            {
              amount = amount + 1
              val position = logArray(i).indexOf(" ")
              logArray(i) = logArray(i).substring(0,position+1) +
              logArray(i).substring(position+1,position+6).capitalize + logArray(i).substring(position+6)
              logArray(i) = logArray(i).replaceFirst("([r|g]): ","$1 - ")
              logArray(i) = logArray(i).replaceFirst("(.*):([0-9]+): (.*)","\n<tr class='" +
                                                     logTrFirst + "'>\n<td class='" + logTd1 +
                                                     "'>$2</td><td class='" + logTd2 +
                                                     "'><a href='http://$1#$2'>$1:$2</a></td>\n</tr>\n<tr class='" +
                                                     logTrSecond + "'>\n<td class='" + logTd1 +
                                                     "'></td><td class='" + logTd2 + "'>$3</td>\n</tr>")
            }
            else if(i % 3 == 1)
            {
              val position = logArray(i+1).indexOf("^")
              logArray(i) = logArray(i).substring(0,position) +
              logArray(i).substring(position).replaceFirst("([A-Za-z0-9_]+)", "<u>$1</u>")
              logArray(i) = "\n<tr class='" + logTrSecond + "'>\n<td class='" + logTd1 +
              "'></td><td class='" + logTd2  + " " + logTdFixed + "'>" + logArray(i) + "</td>\n</tr>"
            }
          }
        }
        if(i % 3 != 2)
        {
          log += logArray(i)
        }
      }
    }
    if(logTrFinalCompile == logTrFinalCompileFailure)
    {
      log += "\n<tr class='" + logTrFinalCompile + "'>\n<td class='" + logTd1 +
      "'></td><td class='" + logTd2 + "'>COMPILATION FAILED</td>\n</tr>"
    }
    else
    {
      log += "\n<tr class='" + logTrFinalCompile + "'>\n<td class='" + logTd1 +
      "'></td><td class='" + logTd2 + "'>COMPILATION SUCCESSFUL</td>\n</tr>"
    }
    log = "<div class='logDiv'>\n<table class='logTable' cellspacing='0'>" + log + "\n</table>\n</div>"
    return log
  }

  def getXMLlog(logFile: String): Node = {
    val xmlLog = XML.loadString(parseLog(logFile))
    return xmlLog
  }
}