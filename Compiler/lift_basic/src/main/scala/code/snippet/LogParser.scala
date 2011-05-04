import java.lang.String

object LogParser{
  var logTest = "warning: there were deprecation warnings; re-run with -deprecation for details\none warning found"
  var logTest2 = "NetBeansProjects/SA4Game/src/sa4game/Server.scala:12: warning: method removeKey in trait MapLike is deprecated: Use `remove' instead\n    ab.removeKey(\"pol\")\n       ^\nNetBeansProjects/SA4Game/src/sa4game/Server.scala:13: warning: method removeKey in trait MapLike is deprecated: Use `remove' instead\n    ab.removeKey(\"pol\")\n       ^\ntwo warnings found"
  var logTest3 = "NetBeansProjects/SA4Game/src/sa4game/Client.scala:17: error: not found: type HashMap\n  var games = new HashMap[String,Array[Array[Array[Character]]]]\n                  ^\nNetBeansProjects/SA4Game/src/sa4game/Client.scala:6969: error: not found: type Random\n    val rnd = new Random\n                  ^\n2 errors found"
  var logTest4 = ""

  def parseLog(string: String): collection.mutable.HashMap[String,String] =
  {
    var amount = 0
    var errorDisplay = "-"
    var warningDisplay = "-"
    var logArray = string.split("\n")
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
    if(logArray(logArray.length-1).endsWith("error found") || logArray(logArray.length-1).endsWith("errors found"))
    {
      logTrFinal = logTrFinalError
      logTrFinalCompile = logTrFinalCompileFailure
      logTrFirst = logTrErrorFirst
      logTrSecond = logTrErrorSecond
    }
    else if(logArray(logArray.length-1).endsWith("warning found") || logArray(logArray.length-1).endsWith("warnings found"))
    {
      logTrFinal = logTrFinalWarning
      logTrFinalCompile = logTrFinalCompileWarning
      logTrFirst = logTrWarningFirst
      logTrSecond = logTrWarningSecond
    }
    var log = ""
    if(logArray.length > 1)
    {
      for (i <- 0 until logArray.length)
      {
        if(i == logArray.length-1)
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
      errorDisplay = amount.toString
      warningDisplay = "?"
      log += "\n<tr class='" + logTrFinalCompile + "'>\n<td class='" + logTd1 +
      "'></td><td class='" + logTd2 + "'>COMPILATION FAILED</td>\n</tr>"
    }
    else
    {
      if(logTrFinalCompile == logTrFinalCompileWarning)
      {
        if(amount == 0)
        {
          warningDisplay = "*"
        }
        else
        {
          warningDisplay = amount.toString
        }
      }
      log += "\n<tr class='" + logTrFinalCompile + "'>\n<td class='" + logTd1 +
      "'></td><td class='" + logTd2 + "'>COMPILATION SUCCESSFUL</td>\n</tr>"
    }
    log = "<div class='logDiv'>\n<table class='logTable' cellspacing='0'>" + log + "\n</table>\n</div>"
    var result = new collection.mutable.HashMap[String,String]
    result("errorDisplay") = errorDisplay
    result("warningDisplay") = warningDisplay
    result("log") = log
    return result
  }

  def main(args: Array[String]): Unit = {
    var parsed = parseLog(logTest)
    println("<p>errors: " + parsed("errorDisplay") + ", warnings: " + parsed("warningDisplay") + "</p>")
    println(parsed("log"))
    println("<p></p>")
    parsed = parseLog(logTest2)
    println("<p>errors: " + parsed("errorDisplay") + ", warnings: " + parsed("warningDisplay") + "</p>")
    println(parsed("log"))
    println("<p></p>")
    parsed = parseLog(logTest3)
    println("<p>errors: " + parsed("errorDisplay") + ", warnings: " + parsed("warningDisplay") + "</p>")
    println(parsed("log"))
    println("<p></p>")
    parsed = parseLog(logTest4)
    println("<p>errors: " + parsed("errorDisplay") + ", warnings: " + parsed("warningDisplay") + "</p>")
    println(parsed("log"))
    println("<p></p>")
  }
}