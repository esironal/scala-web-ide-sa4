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
                 <div class="ProfileContainer">
                 	<div class="profileImg">
              			  <img src={user.personalImageURL} style="width:100px; height:100px;"/>
              		</div>
                    <div class="userData">
                     	<span style="width:70px; left:float;">
                     		<h2>{ user.accountID}</h2>
                     	</span>
                     	<span style="width:70px; left:float;">
                     		{"Name: " }<b>{user.firstName.is + " " + user.lastName.is}</b>
                     	</span>                     						
                     	<span style="width:50px; left:float;">
                     		{"Gender: " }<b>{ user.gender.is }</b>
                     	</span>
                     	<br/>
                    	<span style="width:50px;  left:float;">
                    		{"Email: " }<b>{ user.email.is }</b>
                    	</span>   
                     	<span style="width:50px;  left:float;" >
                     		{"SkypeID: "} <b>{user.skypeID.is}</b>
                     	</span>
                     	<hr/>
                     	<br/>
                     	<br/>
                     	<div class="msgComment">
                      		<div class="lift:FormMessage" id="formMSG"> 
                      		</div>
							<div class="msgsComments">
								{for(message <- msgs) yield(printMessage(message))}
							</div>
                     </div>
                    </div>
                  </div>
                     
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
             	
         	 <div class="msgContainer">
         		<div class="lftSmallImg">
         			<img src={user.personalImageURL} style="width:50px; height:50px;"></img>
         		</div>
         		<div class="rightComments">
         			<span>
         				<b>
         					<a href={"/profile/"+user.accountID.is}>{""+user.accountID.is}</a>
         				</b>
         					{"\t " + msg.date.is + "" + msg.messageNumber.is + "    "}
         			</span>
         			<br/>
         			<span>
         				{"" + msg.text.is}
         			</span>
             </div>
            </div>
            <br/>
         }
     }