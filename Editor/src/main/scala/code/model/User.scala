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
   override def loginXhtml = <form action="/user_mgt/login" method="post">
  <table>
  <tr>
  <td>Email Address</td>
  <td><input name="username" type="text" id="F993430246327ZS4" />
  <script type="text/javascript">
  // <![CDATA[
  jQuery(document).ready(function() {if (document.getElementById("F993430246327ZS4")) {document.getElementById("F993430246327ZS4").focus();};});
  // ]]>
  </script></td></tr>
          <tr>
  <td>Password</td>
  <td><input type="password" name="password" /></td>
  </tr>
          <tr>
  <td><input value="Log In" type="submit" /></td></tr>
  </table>
  </form>	
  override def editFields = List(firstName, lastName, password, gender, skypeID, personalImageURL)	
  override def signupXhtml(user: User) = <form action="/user_mgt/sign_up" method="post">
  <table><tr>
    <td colspan="2">Sign Up</td>
  </tr> 
          <tr>
  <td>Account Name</td>
  <td><input  name="F479282022896OO5" type="text" maxlength="30" value="" /></td>
  </tr>
  <tr>
  <td>First Name</td>
  <td><input id="txtFirstName" name="F479282022897VVR" type="text" maxlength="32" value="" /></td>
  </tr>
  <tr>
  <td>Last Name</td>
  <td><input id="txtLastName" name="F479282022898SNQ" type="text" maxlength="32" value="" /></td>
  </tr>
  <tr>
  <td>Password</td>
  <td><span><input value="*******" type="password" name="F479282022899G0X"  /> Repeat <input value="*******" type="password" name="F479282022899G0X" /></span></td>
  </tr>
  <tr>
  <td>Email Address</td>
  <td><input id="txtEmail" name="F479282022900FI4" type="text" maxlength="48" value="" /></td>
  </tr>
  <tr>
  <td>Gender</td>
  <td><select name="F479282022901Z2L"><option value="TGNKJPT1EZVOX0IW5QNO" selected="selected">Male</option><option value="4GWRCIBHZIJBWTFZQET3">Female</option></select></td>
  </tr>
  <tr>
  <td>Skype ID</td>
  <td><input  name="F479282022902WFS" type="text" maxlength="30" value="" /></td>
  </tr> 
          <tr>
  <td><input name="F479282032903UCE" type="submit" value="Sign Up" /></td>
  </tr> 
                                </table>
  </form>  

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