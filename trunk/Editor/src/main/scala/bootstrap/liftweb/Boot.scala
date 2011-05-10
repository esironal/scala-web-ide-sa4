package bootstrap.liftweb

import net.liftweb._
import util._
import Helpers._

import common._
import http._
import sitemap._
import Loc._
import mapper._

import js._
import JsCmds._
import JE._

import scala.xml.NodeSeq

import code.model._
import code.comet.ChatServer
import scala.collection.immutable._


/**
 * A class that's instantiated early and run.  It allows the application
 * to modify lift's environment
 */
class Boot {
  def boot {
    if (!DB.jndiJdbcConnAvailable_?) {
      val vendor = 
	new StandardDBVendor(Props.get("db.driver") openOr "org.h2.Driver",
			     Props.get("db.url") openOr 
			     "jdbc:h2:lift_proto.db;AUTO_SERVER=TRUE",
			     Props.get("db.user"), Props.get("db.password"))

      LiftRules.unloadHooks.append(vendor.closeAllConnections_! _)

      DB.defineConnectionManager(DefaultConnectionIdentifier, vendor)
    }

    // Use Lift's Mapper ORM to populate the database
    // you don't need to use Mapper to use Lift... use
    // any ORM you want
    Schemifier.schemify(true, Schemifier.infoF _, User)

    // where to search snippet
    LiftRules.addToPackages("code")

    // Build SiteMap
    def sitemap = SiteMap(
      Menu.i("Home") / "index" >> User.AddUserMenusAfter, // the simple way to declare a menu

      // more complex because this menu allows anything in the
      // /static path to be visible
      Menu(Loc("Static", Link(List("static"), true, "/static/index"), 
	       "Static Content")),
	  Menu(Loc("Chat", Link(List("chat"), true, "/chat/"), 
	       "Chat Content"))     )

    def sitemapMutators = User.sitemapMutator

    // set the sitemap.  Note if you don't want access control for
    // each page, just comment this line out.
    LiftRules.setSiteMapFunc(() => sitemapMutators(sitemap))

    //Show the spinny image when an Ajax call starts
    LiftRules.ajaxStart =
      Full(() => LiftRules.jsArtifacts.show("ajax-loader").cmd)
    
    // Make the spinny image go away when it ends
    LiftRules.ajaxEnd =
      Full(() => LiftRules.jsArtifacts.hide("ajax-loader").cmd)

    // Force the request to be UTF-8
    LiftRules.early.append(_.setCharacterEncoding("UTF-8"))
    
    LiftRules.viewDispatch.append {
    	case "chat" :: Nil => Right(ChatView)
    }

    // What is the function to test if a user is logged in?
    LiftRules.loggedInTest = Full(() => User.loggedIn_?)

    // Make a transaction span the whole HTTP request
    S.addAround(DB.buildLoanWrapper)
  }
  
  object ChatView extends LiftView {
  	def dispatch = {
  		case filename:String => () => Full(content(filename))	
  	}
  	
  	def content(filename:String):NodeSeq = {	
  	 <lift:surround with="default" at="content">
		<!--<hr>-->
		<table id="main_table">
			<tr>
				<td id="left_sidebar" value="true">
				FILE BROWSER
				</td>			
				<td id="codearea">
					<textarea id={filename} name={filename} class="editor"></textarea>
					<div id="log">
						<div class='logDiv'>

						<table class='logTable' cellspacing='0'>

						<tr class='logTrErrorFirst'>

						<td class='logTd1'>17</td><td class='logTd2'><a href='http://NetBeansProjects/SA4Game/src/sa4game/Client.scala#17'>NetBeansProjects/SA4Game/src/sa4game/Client.scala:17</a></td>

						</tr>

						<tr class='logTrErrorSecond'>

						<td class='logTd1'></td><td class='logTd2'>Error - not found: type HashMap</td>

						</tr>

						<tr class='logTrErrorSecond'>

						<td class='logTd1'></td><td class='logTd2 logTdFixed'>  var games = new <u>HashMap</u>[String,Array[Array[Array[Character]]]]</td>

						</tr>

						<tr class='logTrErrorFirst'>

						<td class='logTd1'>12345</td><td class='logTd2'><a href='http://NetBeansProjects/SA4Game/src/sa4game/Client.scala#12345'>NetBeansProjects/SA4Game/src/sa4game/Client.scala:12345</a></td>

						</tr>

						<tr class='logTrErrorSecond'>

						<td class='logTd1'></td><td class='logTd2'>Error - not found: type Random</td>

						</tr>

						<tr class='logTrErrorSecond'>

						<td class='logTd1'></td><td class='logTd2 logTdFixed'>    val rnd = new <u>Random</u></td>

						</tr>

						<tr class='logTrFinalError'>

						<td class='logTd1'></td><td class='logTd2'>2 errors found</td>

						</tr>

						<tr class='logTrFinalCompileFailure'>

						<td class='logTd1'></td><td class='logTd2'>COMPILATION FAILED</td>

						</tr>

						</table>

						</div>
						
					</div>
				</td>
					<td id="right_sidebar" value="true">
					<div id="chat_content">
					<lift:comet type="Chat" name={filename}>
				        <h5 style="text-align:center">Chat</h5>
				        <ul>
				          <li>A message</li>
				        </ul>
				     </lift:comet>
				      
					  <div>
				        <form class="lift:ChatIn?form=post">
				          <input id="chat_in" />
					      <input type="hidden" name="filename" value={filename}/>
					      <input type="hidden" name="name" value="Jack"/>
				          <input type="submit" value="Send"/>
				        </form>
				      </div>
					</div>
					</td>
				</tr>
			</table>
			<div id="box" class="dialog">
			<div style="text-align:center"><span id="txt">A nice file viewer</span><br />
			<button onclick="hm('box');okSelected()">OK</button></div>
			</div>
      </lift:surround>
  	}	
  }
}
