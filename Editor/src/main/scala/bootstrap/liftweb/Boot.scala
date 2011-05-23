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

import code.model.User
import code.model.ProfileMessage
import code.model.Project
import code.model.UserProject
import code.model.FileChatMessage
import code.model.ProjectChatMessage

import code.comet._


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

	LiftRules.useXhtmlMimeType = false

    // Use Lift's Mapper ORM to populate the database
    // you don't need to use Mapper to use Lift... use
    // any ORM you want
    Schemifier.schemify(true, Schemifier.infoF _, User, ProfileMessage, Project, UserProject, ProjectChatMessage, FileChatMessage)
    
    // where to search snippet
    LiftRules.addToPackages("code")

    // Build SiteMap
     val loggedIn = If(() => User.loggedIn_?, 
     					() => RedirectResponse("/user_mgt/login"))
    def sitemap = SiteMap(
      Menu.i("Home") / "index" >> User.AddUserMenusAfter, // the simple way to declare a menu

      // more complex because this menu allows anything in the
      // /static path to be visible
      Menu(Loc("Static", Link(List("static"), true, "/static/index"), 
	       "Static Content",loggedIn)),
	      
     Menu(Loc("Profile",
              Link(List("profile"), true, "/profile/"), "Profile",loggedIn)),
     Menu(Loc("Stats",
              Link(List("stats"), true, "/stats/"), "Stats" ,loggedIn)),
     Menu(Loc("UserList",
              Link(List("userList"), true, "/userList/" ), "UserList" ,loggedIn)),
     Menu(Loc("Delete", 
              Link(List("delete"), true, "/delete/"), "Delete" ,loggedIn)),
     Menu(Loc("Project", Link(List("project"), true, "/project"), "Project" ,loggedIn)),
     Menu(Loc("ProjectList",
              Link(List("projectList"), true, "/projectList"), "ProjectList" ,loggedIn)),
     Menu(Loc("Invite",
               Link(List("invite"), true, "/invite"), "Invite")),
        	   
     Menu(Loc("Editor", Link(List("editor"), true, "/editor/"), 
	       "Editor Content" ,loggedIn))     )


	
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
    	case "editor" :: Nil => 
    		Right(Editor)
    	case "profile"::Nil =>
       		Right(Profile)
     	case "stats"::Nil =>
     		Right(Stats)
     	case "delete" :: Nil =>
     		Right(Delete)
     	case "userList" :: Nil =>
     		Right(UserList)
    }

    // What is the function to test if a user is logged in?
    LiftRules.loggedInTest = Full(() => User.loggedIn_?)

    // Make a transaction span the whole HTTP request
    S.addAround(DB.buildLoanWrapper)
    
    // start sefver for incoming messages from compiler
    val server = new Messager(8082)
	server.start
	
	//--- start of compile request
	
	// setting parameters (in this example just one: -verbose)
	//val parameters = new Array[String](1)
	//parameters(0) = "-verbose"
	  
	// userID is a Long  
	//val userID = 1
	
	// projectName is a String
	//val projectName = "project_2"
	  
	// returns a Tuple2(binPath:String,logPath:String)
	//val compile = code.comet.FileManager.compile(projectName, userID, parameters)

	//--- end of compile request
    
    
  }
}