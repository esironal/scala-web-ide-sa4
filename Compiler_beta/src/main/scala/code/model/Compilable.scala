package code
package model
 
import net.liftweb._
import util._
import Helpers._
import common._
import json._

import scala.xml.Node

import snippet._

/**
 * An object to compile
 *
 * @author Raphael Schapfel
 */
case class Compilable(id: String, 
					  path: String,
                      options: Array[String])
 
/**
 * The Compilable companion object
 *
 * @author Raphael Schapfel
 */
object Compilable {
	
	private implicit val formats = net.liftweb.json.DefaultFormats
 
	private var links: List[Compilable] = parse(data).extract[List[Compilable]]
 
	private var listeners: List[Compilable => Unit] = Nil
 
	/**
	 * Convert a JValue to a Compilable if possible
	 */
	def apply(in: JValue): Box[Compilable] = Helpers.tryo{in.extract[Compilable]}
 
	/**
   	 * Extract a String (id) to a Compilable
   	 */
  	def unapply(id: String): Option[Compilable] = Compilable.find(id)
 
  	/**
   	 * Extract a JValue to a Compilable
   	 */
  	def unapply(in: JValue): Option[Compilable] = apply(in)
 
	/**
   	 * The default unapply method for the case class.
     * We needed to replicate it here because we
     * have overloaded unapply methods
     */
	def unapply(in: Any): Option[(String, String, Array[String])] = {
    	in match {
      		case i: Compilable => Some((i.id, i.path, i.options))
      		case _ => None
    	}
	}
 
	/**
     * Convert a Compilable to XML
     */
	implicit def toXml(link: Compilable): Node = <link>{Xml.toXml(link)}</link>
 
  	/**
     * Convert the link to JSON format.  This is
     * implicit and in the companion object, so
     * a Compilable can be returned easily from a JSON call
     */
	implicit def toJson(link: Compilable): JValue = Extraction.decompose(link)
 
	/**
     * Convert a Seq[Compilable] to JSON format.  This is
     * implicit and in the companion object, so
     * a Compilable can be returned easily from a JSON call
     */
	implicit def toJson(links: Seq[Compilable]): JValue = Extraction.decompose(links)
 
	/**
   	 * Convert a Seq[Compilable] to XML format.  This is
     * implicit and in the companion object, so
     * a Compilable can be returned easily from an XML REST call
     */
	implicit def toXml(links: Seq[Compilable]): Node =
		<links>{
      		links.map(toXml)
    	}</links>
 
	/**
     * Get all the links in inventory
     */
  	def inventoryCompilables: Seq[Compilable] = links
 
  	// The raw data
  	private def data = """[]"""

	/**
	 * Compile the Compilable
	 */
  	def compile(link: Compilable): Compilable = synchronized {
		var id = link.id
		var path = link.path
		var opt = link.options
  		var rc = new RunCompiler(id, path, opt)
  		scala.actors.Actor.actor{rc.run()}
		add(link)
	}
 
	/**
     * Select a random Compilable
     */
  	def randomCompilable: Compilable = synchronized {
    	links(Helpers.randomInt(links.length))
  	}
 
	/**
     * Find a Compilable by id
     */
  	def find(id: String): Box[Compilable] = synchronized {
    	links.find(_.id == id)
  	}
 
	/**
     * Add a Compilable to inventory
     */
	def add(link: Compilable): Compilable = {
    	synchronized {
      		links = link :: links.filterNot(_.id == link.id)
      		updateListeners(link)
    	}
  	}

	/**
   	 * Process a Compilable
     */
  	def process(link: Compilable): Compilable = {
    	synchronized {
      		links = link :: links.filterNot(_.id == link.id)
      		updateListeners(link)
    	}
  	}
 
  	/**
     * Find all the Compilables with the string in their id or path
     */
  	def search(str: String): List[Compilable] = {
    	val strLC = str.toLowerCase()
    	links.filter(i =>
      		i.id.toLowerCase.indexOf(strLC) >= 0 ||	i.path.toLowerCase.indexOf(strLC) >= 0)
  	}
 
  	/**
     * Deletes the Compilable with id and returns the
     * deleted Compilable or Empty if there's no match
     */
  	def delete(id: String): Box[Compilable] = synchronized {
    	var ret: Box[Compilable] = Empty
 
    	val Id = id // an upper case stable ID for pattern matching
 
    	links = links.filter {
      		case i@Compilable(Id, _, _) => 
        		ret = Full(i) // side effect
        		false
      		case _ => true
    	}
 		var ch = new CompilerHelper(Integer.parseInt(id), scala.Array(""));
		ch.destroyCompilerBox()
    	ret.map(updateListeners)
  	}
 
  	/**
     * Update listeners when the data changes
     */
  	private def updateListeners(link: Compilable): Compilable = {
    	synchronized {
      		listeners.foreach(f => Schedule.schedule(() => f(link), 0 seconds))
      		listeners = Nil
    	}
    	link
	}
 
	/**
     * Add an onChange listener
     */
  	def onChange(f: Compilable => Unit) {
    	synchronized {
      		// prepend the function to the list of listeners
      		listeners ::= f
    	}
  	} 
}
