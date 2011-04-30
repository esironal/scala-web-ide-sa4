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

import scala.xml.NodeSeq

import code.model.User
import code.model.Message

import code.lib.UserProfileId

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
     Menu(Loc("Stats",
              Link(List("stats"), true, "/stats/"), "Stats")),
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


     LiftRules.viewDispatch.append {
     case "profile"::Nil =>
       	Right(View.Profile)
     case "stats"::Nil =>
     	Right(View.Stats)
   }



   
 }
 
 object View {
     
     
     object Profile extends LiftView {
     	
         def content(id: String) = {
         	val userProfile: Box[User] = User.find(By(User.accountID,id))
         	val user: User = userProfile.openOr(null)
         	
         	var msgs :List[Message] = null
         	
         	if(userProfile!= Empty){
         		UserProfileId.name(id)
         		msgs = Message.findAll(By(Message.userID, user.id.is))
         	}
         	
         	
           S.notice(UserProfileId.name.is)
	
         <html>
 			<head>
   				<meta content="text/html; charset=UTF-8" http-equiv="content-type" />
   				<title/>
 			</head>
 			<body class="lift:content_id=main">
 		  		<div id="main" class="lift:surround?with=default;at=content">
                 {if (User.loggedIn_?){
                 {if(userProfile!= Empty){
                     <h2>{"Profile: " + user.firstName + " " + user.lastName}</h2>
                     <br></br>
                     <h3>{"Email: " + user.email.is }</h3>
                     <h3>{"Password: " + user.password.is }</h3>
                     <h3>{"SkypeID: " + user.skypeID.is }</h3>
                     
                     <span class="lift:FormMessage"> </span>
                     
                     <table border="1">{for(message <- msgs) yield(printMessage(message))
                     }</table>
                     
                     
                     }else 
                     <h2>{"User doesn't exist"}</h2>
                     }}else{
	                    	S.redirectTo("/user_mgt/login")
                     }}
                </div>
 			</body>
		</html>        
             
         }    
         
         def dispatch = { 
             case id: String => () => Full(content(id))
         }
         
         def printMessage(msg: Message) : NodeSeq = {
        	 
        	 val fromUser : Box[User] = User.find(By(User.accountID, msg.fromID.is))
        	 val user : User = fromUser.openOr(null)
        	 <tr>
        	 <td>{msg.messageNumber}</td>
        	 <td>{user.accountID.is}</td>
        	 <td>{msg.date.is}</td>
        	 </tr>
         }
     }
     
   	object Stats extends LiftView {
     	def content() = {   
     		val totalAccount = User.count 
     		val maleAccount: List[User] = User.findAll(By(User.gender, Genders.Male))
     		val femaleAccount: List[User] = User.findAll(By(User.gender, Genders.Female))
     		
     		<html>
 			<head>
   				<meta content="text/html; charset=UTF-8" http-equiv="content-type" />
   				<title>Home</title>
 			</head>
 			<body class="lift:content_id=main">
 		  		<div id="main" class="lift:surround?with=default;at=content">
 		  			<h2>{"We have " + totalAccount + " registered account"}</h2>
 		  			<h3>{(totalAccount - maleAccount.length - femaleAccount.length) + " didn't set their gender"}</h3>
 		  			<h3>{maleAccount.length + " are male"}</h3>
 		  			{printUsers(maleAccount)}
 		  			<h3>{femaleAccount.length + " are female"}</h3>
 		  			{printUsers(femaleAccount)}
 		  		</div>
 			</body>
			</html>        
     	}
     	
     	def printUsers(users: List[User]): NodeSeq = {
     		<span> {
     				for(user <- users)yield(<h5>{user.toString}</h5>)
     			   }
     		</span>
     	}
     	
     	def dispatch = { 
             case _ => () => Full(content())
        }
   	}
 }
 
 
}