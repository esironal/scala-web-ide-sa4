package bootstrap.liftweb

import net.liftweb._
import util._
import Helpers._

import common._
import http._
import sitemap._
import Loc._
import mapper._

import code.model._
import scala.xml.NodeSeq
import code.model.Message

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
    Schemifier.schemify(true, Schemifier.infoF _, User, Message)

    // where to search snippet
    LiftRules.addToPackages("code")

    // Build SiteMap
    def sitemap = SiteMap(
      Menu.i("Home") / "index" >> User.AddUserMenusAfter, // the simple way to declare a menu

      // more complex because this menu allows anything in the
      // /static path to be visible
      Menu(Loc("Profile",
               Link(List("profile"), true, "/profile/"), "Profile")),
//      Menu.i("Rewritten Example") / "profile2" >> User.AddUserMenusAfter >> Hidden,
      Menu(Loc("Static", Link(List("static"), true, "/static/index"), 
	       "Static Content")))

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

    
//      LiftRules.statelessRewrite.append {
//      case RewriteRequest(
//        ParsePath(List("profile", id), _, _, _), _, _) =>
//          RewriteResponse("profile2"::Nil, Map("id" -> id))
//    } 
//    
  	LiftRules.viewDispatch.append {
      case "profile"::Nil =>
        Right(View.Profile)
     
    }



    
    
  }
  
  object View {
  	
  	
  	object Profile extends LiftView {
  		def content(id: String) = 
  		{
  			val userProfile: Box[User] = User.find(By(User.accountName, id))
  			val user: User = userProfile.openOr(null)
  			val msgs = Message.findAll(By(Message.userID, user.id.is))
  		
  		
  		<html>
  			<head>
    		<meta content="text/html; charset=UTF-8" http-equiv="content-type" />
    		<title>Home</title>
  			</head>
  			<body class="lift:content_id=main">
    		<div id="main" class="lift:surround?with=default;at=content">
      		
      		{
      		if(User.loggedIn_?) {	
      			if(userProfile != Empty)  {
      				<h2>{"Profile: "+ id} </h2>
      			}
      			else { 
      				<h2>User does not exist.</h2>
      			}
      		}
      		else {
      			S.redirectTo("/user_mgt/login")
      			
      		}
      		}
      		
  			<div class="lift:FormMessage">
  
  			
  			
  			{
  				
  				for(msg <- msgs) yield(<h3>{msg.toString}</h3>)
  			}
	 		</div>
  			
    		</div>
  		</body>
	</html>    	
  			
  		}	
  		
  		def dispatch = {
  				
  			case id: String => () => Full(content(id))
  		}
  		
  	
  	}
//  	


  }
  
  
  
}
