package bootstrap.liftweb

import net.liftweb._
import net.liftweb.mapper.Genders
import util._
import Helpers._

import common._
import http._
import sitemap._
import Loc._
import mapper._

import code.model.User
import code.model.ProfileMessage
import code.model.Project
import code.model.UserProject
import code.model.FileChatMessage
import code.model.ProjectChatMessage


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
   Schemifier.schemify(true, Schemifier.infoF _, User, ProfileMessage, Project, UserProject, ProjectChatMessage, FileChatMessage)

   // where to search snippet
   LiftRules.addToPackages("code")

   // Build SiteMap
   def sitemap = SiteMap(
     Menu.i("Home") / "index" >> User.AddUserMenusAfter, // the simple way to declare a menu

     // more complex because this menu allows anything in the
     // /static path to be visible
     Menu(Loc("Profile",
              Link(List("profile"), true, "/profile/"), "Profile")),
     Menu(Loc("Stats",
              Link(List("stats"), true, "/stats/"), "Stats")),
     Menu(Loc("UserList",
              Link(List("userList"), true, "/userList/"), "UserList")),
     Menu(Loc("Delete",
              Link(List("delete"), true, "/delete/"), "Delete")),
     Menu(Loc("Static", Link(List("static"), true, "/static/index"), 
          "Static Content")),
     Menu(Loc("Project", Link(List("project"), true, "/project"), "Project")),
     Menu(Loc("ProjectList",
              Link(List("projectList"), true, "/projectList"), "ProjectList")))

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

   // What is the function to test if a user is logged in?
   LiftRules.loggedInTest = Full(() => User.loggedIn_?)

   
   // Use HTML5 for rendering
   LiftRules.htmlProperties.default.set((r: Req) =>
     new Html5Properties(r.userAgent))    

   // Use jQuery 1.4
   LiftRules.jsArtifacts = net.liftweb.http.js.jquery.JQuery14Artifacts

   
   // Make a transaction span the whole HTTP request
   S.addAround(DB.buildLoanWrapper)


     LiftRules.viewDispatch.append {
     case "profile"::Nil =>
       	Right(Profile)
     case "stats"::Nil =>
     	Right(Stats)
     case "delete" :: Nil =>
     	Right(Delete)
     case "userList" :: Nil =>
     	Right(UserList)
     
   }
   val test = new ProjectXML
   println(test.finalHTML("/ciao"))
   
   
    //start messager
    val messager = new code.comet.Messager(8082)
    messager.start
   
 }
}