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
        	 
        	 if(id == "index") {
        		 
        		if(User.find(By(User.accountID,id)) == Empty) {
        			
        			S.redirectTo("/profile/" + User.currentUser.openOr(null).accountID.is)
        		}
        	 }
        	 
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
         					
         					
         			<div id="ProfileContainer" style="height:378px;">
         					<div class="profileImg">
         						<img src={user.personalImageURL} style="width:140px; height:140px;"/>
         					</div>
         					
         					<div class="userData fontTahoma">
         						<span style="font-weight:bold; font-size:20px; width:320px;">
         							<h2 style="text-align:left;">{ user.accountID}</h2>
         						</span>
         						<div class="userInfo">
         							<div class="userDataLeft">
         								<span><b>Name: </b></span>{user.firstName.is + " " + user.lastName.is}
         								<br />
         								<span><b>Email: </b></span>{ user.email.is }
         							</div>
         							<div class="userDataRight">
         								<span><b>&nbsp;&nbsp; Gender: </b></span>{ user.gender.is }
         								<br/>
         								<span><b>&nbsp;&nbsp; SkypeID: </b></span>{ user.skypeID.is }
         							</div>
         						</div>
         					</div>
                    <br />
         			     <div class="msgContainer">
                             <div class="msgLeftCol"></div>

                             <div class="formMsg">
                                 <span class="fontTahoma" style="text-align:left;margin-right:136px">Post something on my Profile</span>
                                 <lift:FormMessage> </lift:FormMessage>
                             </div>
                        </div>
                
         	    <div id="msgsContainer" style="width:590px;">
         		<div class="msgLeftCol"></div>
         		
                   {for(message <- msgs) yield(printMessage(message))}
                </div>
                </div>
                     }else 
                     <h2>{"User doesn't exist" + "  /" +id + "/"
                    	 
                     }</h2>
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
         	
         	<div id="message">
         		<div class="messageImg">
         			<img src={user.personalImageURL} style="width:64px; height:64px;"/>
         		</div>
         		<div class="messageInfo">
         			<span class="msgFrom"><b><a href={"/profile/"+user.accountID.is}>
         				{""+user.accountID.is}
         			   </a></b>
         			</span>
         			<span>
         				{"\t " + msg.date.is}
         		    </span>
         	   </div> 
         	   <div class="messageText">
         			{"" + msg.text.is}
         	   </div>
         	</div>
         	<hr class="msgHr"/>
         	
         	
         }
     }