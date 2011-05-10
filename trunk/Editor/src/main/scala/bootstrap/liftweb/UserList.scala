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
