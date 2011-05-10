package code
package comet



class Function

case class Save(path: String) extends Function
case class Delete(path: String) extends Function
case class Compiled(id: Int, path: String) extends Function
	
