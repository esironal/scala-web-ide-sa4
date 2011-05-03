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
     Menu(Loc("UserList",
              Link(List("userList"), true, "/userList/"), "UserList")),
     Menu(Loc("Delete",
              Link(List("delete"), true, "/delete/"), "Delete")),
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
     case "delete" :: Nil =>
     	Right(View.Delete)
     case "userList" :: Nil =>
     	Right(View.UserList)
   }
   
 }
 
 object View {
     
     
     object Profile extends LiftView {
     	
         def content(id: String) = {
         	val userProfile: Box[User] = User.find(By(User.accountID,id))
         	val user: User = userProfile.openOr(null)
         	
         	var msgs :List[Message] = null
         	
         	if(userProfile!= Empty){
         		msgs = Message.findAll(By(Message.userID, user.id.is)).reverse
         	}
         	
         	
         <html>
 			<head>
   				<meta content="text/html; charset=UTF-8" http-equiv="content-type" />
   				<title>Home</title>
 			</head>
 			<body class="lift:content_id=main">
 		  		<div id="main" class="lift:surround?with=default;at=content">
                 {if (User.loggedIn_?){
                 {if(userProfile!= Empty){
                 <div class="ProfileContainer">	<div class="profileImg">
                <img src={user.personalImageURL} style="width:100px; height:100px;"/></div>
                     <div class="userData">
                     <h2>{ user.accountID}</h2>
                     <div style="float:left;">{"Name: " +user.firstName.is + " " + user.lastName.is}</div>
                     <div style="float:left;">{"Gender: " + user.gender.is }</div>
                     <br/>
                     <div style="float:left;">{"Email: " + user.email.is }</div>
                    
                     <div style="float:left;">{"SkypeID: " + user.skypeID.is }</div>
                     
                    
                     </div>
                     
                     <br />
                      <div class="lift:FormMessage"> </div>
                     <hr/>
                     </div>
                     <div>{for(message <- msgs) yield(printMessage(message))
                     }</div>
                     
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
         
          def printMessage(msg: Message): NodeSeq = {
         	
         	val fromUser: Box[User] = User.find(By(User.id, msg.fromID.is))
         	val user: User = fromUser.openOr(null)
         	
         	<span>
         	<img src={user.personalImageURL} style="width:64px; height:64px;"></img><span>{"" + msg.messageNumber.is + ":  "}</span><b><a href={"/profile/"+user.accountID.is}>{""+user.accountID.is}</a></b><span>{"\t " + msg.date.is}</span>
         	<br/>
         	<span>{"" + msg.text.is}</span>
         	<hr/>
         	</span>
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
   	
   	object UserList extends LiftView {
     	def content() = {   
			val users: List[User] = User.findAll()
			
			
			
			<html>
 			<head>
   				<meta content="text/html; charset=UTF-8" http-equiv="content-type" />
   				<title>Home</title>
 			</head>
 			<body class="lift:content_id=main">
 		  		<div id="main" class="lift:surround?with=default;at=content">
 		  		
 		  		<span>{
 		  			
 		  			for(user <- users) yield(
						<span>{"#   "}</span>
						<b><a href={"/profile/"+user.accountID.is}>{""+user.accountID.is}</a></b>
						<span>{"   " + user.firstName.is + " " + user.lastName.is}</span>
						<br/>
						)}
				</span>
			</div>
 			</body>
			</html> 
		
     	}
     	
     	def dispatch = { 
             case _ => () => Full(content())
        }
   	}

   	
   	object Delete extends LiftView {
     	def content() = {   
     		
     		val users: List[User] = User.findAll()
     		val msgs: List[Message] = Message.findAll()
     		
     		for(user <- users) User.delete_!(user)
     		for(msg <- msgs)  Message.delete_!(msg)
     	
     	
     		<html>
 			<head>
   				<meta content="text/html; charset=UTF-8" http-equiv="content-type" />
   				<title>Home</title>
 			</head>
 			<body class="lift:content_id=main">
 		  		<div id="main" class="lift:surround?with=default;at=content">
 		  			<span>{"User: " + User.count}</span><br/>
 		  			<span>{"UserList: " + users.length}</span><br/>
 		  			<span>{"Message: " + Message.count}</span><br/>
 		  		</div>
 			</body>
			</html>      
     	
     	
     	}
     	
     	def dispatch = { 
             case _ => () => Full(content())
        }
   	}
 }
 
}