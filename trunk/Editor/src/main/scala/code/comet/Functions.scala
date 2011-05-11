package code
package comet



class Function

case class Save(path: String) extends Function
case class Delete(path: String) extends Function
case class Compile(path: String, userId: Long, options: Array[String]) extends Function
