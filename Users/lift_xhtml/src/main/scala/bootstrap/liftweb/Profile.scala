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
import code.model.ProfileMessage

object Profile extends LiftView {
     	
         def content(id: String) = {
         	val userProfile: Box[User] = User.find(By(User.accountID,id))
         	val user: User = userProfile.openOr(null)
         	
         	var msgs :List[ProfileMessage] = null
         	
         	if(userProfile!= Empty){
         		msgs = ProfileMessage.findAll(By(ProfileMessage.userID, user.id.is)).reverse
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
         
          def printMessage(msg: ProfileMessage): NodeSeq = {
         	
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