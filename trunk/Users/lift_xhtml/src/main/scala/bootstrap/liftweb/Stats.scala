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
   	