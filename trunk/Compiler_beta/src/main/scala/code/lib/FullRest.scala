package code
package lib
 
import model._
import snippet._
 
import net.liftweb._
import common._
import http._
import rest._
import util._
import Helpers._
import json._
import scala.xml._
 
/**
 * The full REST server to compile and return the binary files
 * and the log of the compilation.
 *
 * @author Raphael Schapfel
 */
object FullRest extends RestHelper {

	// Serve /compiler/link and friends
	serve( "compiler" / "link" prefix {
     
		// /compiler/link returns all the items
		case Nil JsonGet _ => Compilable.inventoryCompilables: JValue

		// /compiler/link/count gets the link count
		case "count" :: Nil JsonGet _ => JInt(Compilable.inventoryCompilables.length)
 
		// /compiler/link/link_id gets the specified link (or a 404)
		case Compilable(link) :: Nil JsonGet _ => link: JValue
 
		// /compiler/link/search/foo or /compiler/link/search?q=foo
		case "search" :: q JsonGet _ =>
			(for {
				searchString <- q ::: S.params("q")
				link <- Compilable.search(searchString)
			} yield link).distinct: JValue
 
		// DELETE the link in question
		case Compilable(link) :: Nil JsonDelete _ => 
			Compilable.delete(link.id).map(a => a: JValue)
 
		// // PUT adds the link if the JSON is parsable
		// 	case Nil JsonPut Compilable(link) -> _ => Compilable.add(link): JValue
     
		// PUT adds the link if the JSON is parsable
		case Nil JsonPut Compilable(link) -> _ => Compilable.compile(link): JValue
		
		// POST if we find the link, merge the fields from 
		// the POST body and update the link
		case Compilable(link) :: Nil JsonPost json -> _ => 
			Compilable(mergeJson(link, json)).map(Compilable.add(_): JValue)
 
		// Wait for a change to the Compilables
		// But do it asynchronously
		case "change" :: Nil JsonGet _ =>
			RestContinuation.async {
				satisfyRequest => {
					// schedule a "Null" return if there's no other answer
					// after 110 seconds
					Schedule.schedule(() => satisfyRequest(JNull), 110 seconds)
 
					// register for an "onChange" event.  When it
					// fires, return the changed link as a response
					Compilable.onChange(link => satisfyRequest(link: JValue))
				}
			}
	})
}