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

object Delete extends LiftView {
     	def content() = {   
     		
     		val users: List[User] = User.findAll()
     		val msgs: List[ProfileMessage] = ProfileMessage.findAll()
     		
     		for(user <- users) User.delete_!(user)
     		for(msg <- msgs)  ProfileMessage.delete_!(msg)
     	
     	
     		<html>
 			<head>
   				<meta content="text/html; charset=UTF-8" http-equiv="content-type" />
   				<title>Home</title>
 			</head>
 			<body class="lift:content_id=main">
 		  		<div id="main" class="lift:surround?with=default;at=content">
 		  			<span>{"User: " + User.count}</span><br/>
 		  			<span>{"UserList: " + users.length}</span><br/>
 		  			<span>{"Message: " + ProfileMessage.count}</span><br/>
 		  		</div>
 			</body>
			</html>      
     	
     	
     	}
     	
     	def dispatch = { 
             case _ => () => Full(content())
        }
}  	
