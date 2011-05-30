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
import code.lib._


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
	       "Static Content",loggedIn, Hidden)),
	      
     Menu(Loc("Profile",
              Link(List("profile"), true, "/profile/"), "Profile",loggedIn)),
     Menu(Loc("Stats",
              Link(List("stats"), true, "/stats/"), "Stats" ,loggedIn, Hidden)),
     Menu(Loc("UserList",
              Link(List("userList"), true, "/userList/" ), "UserList" ,loggedIn, Hidden)),
     Menu(Loc("Delete", 
              Link(List("delete"), true, "/delete/"), "Delete", loggedIn, Hidden)),
     Menu(Loc("Project", Link(List("project"), true, "/project"), "Project", loggedIn, Hidden)),
     Menu(Loc("ProjectList",
              Link(List("projectList"), true, "/projectList"), "ProjectList" ,loggedIn, Hidden)),
     Menu(Loc("Invite",
               Link(List("invite"), true, "/invite"), "Invite", loggedIn, Hidden)),
        	   
     Menu(Loc("Editor", Link(List("editor"), true, "/editor/"), 
	       "Editor Content" , loggedIn, Hidden))     )


	
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
    		Left(Editor.newEditor _)
		case "editor" :: _ :: Nil => 
	    	S.redirectTo("/index.html")
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
    
    // find localhost:8080/zipped/name.zip
    LiftRules.statelessDispatchTable.append {
      case Req("zipped" :: name :: Nil,"zip", GetRequest) =>
        () =>
        for {
          stream <- tryo(new java.io.FileInputStream("projectfiles/"+name+".zip")) if null ne stream
        } yield StreamingResponse(stream, () => stream.close, stream.available, List("Content-Type" -> "application/zip"), Nil, 200)
    }
    
    // REST for compiling
    LiftRules.dispatch.append(RestCompiler) // stateful -- associated with a servlet container session
    LiftRules.statelessDispatchTable.append(RestCompiler) // stateless -- no session created
    
    
    // start sefver for incoming messages from compiler
    val server = new Messager(8082)
	server.start
	
    
    
  }
}
