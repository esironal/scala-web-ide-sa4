package code
package model

import net.liftweb.mapper._
import net.liftweb.util._
import net.liftweb.common._
import net.liftweb._

import util._
import Helpers._

import common._
import http._
import sitemap._
import Loc._
import mapper._

/**
 * The singleton that has methods for accessing the database
 */
object User extends User with MetaMegaProtoUser[User] {
  override def dbTableName = "users" // define the DB table name
  override def screenWrap = Full(<lift:surround with="default" at="content">
			       <lift:bind /></lift:surround>)
  
  //Order
  override def fieldOrder = List(id, accountID, firstName, lastName, email,
  locale, timezone, password, skypeID, gender, personalImageURL)

  //Signup and edit
  override def signupFields = List(accountID, firstName, lastName, password, email, gender, skypeID)	
  override def editFields = List(firstName, lastName, password, gender, skypeID, personalImageURL)	

  override def loginXhtml = <div class="logInBox"><div class="logInHome">
  								<form action="/user_mgt/login" method="post">
	  							<table> 
	  								<tr><td class="fontTahoma">Email Address</td>
	  									<td class="marginLeft"><input  name="username" type="text" id="F20368288323150F" />
	  									<script type="text/javascript"> 
	  										// <![CDATA[
	  										jQuery(document).ready(function() {if (document.getElementById("F20368288323150F")) {document.getElementById("F20368288323150F").focus();};});
	  											// ]]>
	  									</script></td></tr> 
          							<tr><td class="fontTahoma"> Password</td><td class="marginLeft"><input type="password" name="password" /></td></tr> 
          							<tr><td></td><td><input id="btnLogin" value="Log In" type="submit" /></td></tr></table> 
  								</form>
	  							
	  							</div></div> 
	 
  override def signupXhtml(user : User) = <div class="signUpBox">
  												<div class="signUpHome fontTahoma">
  													{ super.signupXhtml(user) }
  												</div>
  										   </div>  										
   override def editXhtml(user : User) = <div class="editBox fontTahoma">
  												<div class="editPage">
  													{ super.editXhtml(user) }
  												</div>
  										   </div>  	
  
  
  override def changePasswordXhtml = <div id="changeBox"><div class="changePage fontTahoma">{ super.changePasswordXhtml }</div> </div>
  override def lostPasswordXhtml = <div id="lostBox" class="fontTahoma"><div class="marginTop">{ super.lostPasswordXhtml }</div></div> 
 
  override def skipEmailValidation = true
}

class User extends MegaProtoUser[User] with ManyToMany {
  	def getSingleton = User
  	
	object accountID extends MappedString(this, 30){
		override def displayName = "Account Name"
		override def validations = valUnique(S.?("This name is already taken")) _ :: super.validations
	}
	

	object skypeID extends MappedString(this, 30) {
		override def displayName = "Skype ID"
	}
	
	object gender extends MappedGender(this){
		override def displayName = "Gender"
	}
	
	object personalImageURL extends MappedString(this, 200){
		override def displayName = "Personal Image"
		override def defaultValue = "/images/img.png" 
	}
	
	object coordinates extends MappedString(this, 100){
		override def displayName = "Coordonates"
		override def defaultValue = "NotSet" 
	}

	object projects extends MappedManyToMany(UserProject, UserProject.user, UserProject.project, Project)
}