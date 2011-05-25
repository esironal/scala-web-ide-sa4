package code
package model

import net.liftweb.mapper._
import net.liftweb.util._
import net.liftweb.common._
import net.liftweb._

import util._
import Helpers._
import scala.xml._
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
//  override def loginFields = List(email, password)
  override def signupFields = List(accountID, firstName, lastName, password, email, gender, skypeID)	
  override def editFields = List(firstName, lastName, password, gender, skypeID, personalImageURL)	

  
  
  
  override def localForm(user: User, ignorePassword: Boolean, fields: List[FieldPointerType]): NodeSeq = {   
    for {   
      pointer <- fields   
      field <- computeFieldFromPointer(user, pointer).toList   
      if field.show_? && (!ignorePassword || !pointer.isPasswordField_?)   
      form <- field.toForm.toList   
    } yield { 
     <tr><td>{field.displayName}</td>
     	<td>{form}<lift:Msg id={field.uniqueFieldId.get} errorClass="errorMessage" />
     </td></tr> 
    } 
  }   
  

  
  override def loginXhtml = <div class="logInBox"><div class="logInHome">
	  						<form action="/user_mgt/login" method="post">
	  						<table>
	  							<tr>
	  								<td>Email Address</td>
	  								<td><input name="username" type="text" id="F993430246327ZS4" />
	  									<script type="text/javascript">
	  									//	 <![CDATA[
	  											jQuery(document).ready(function() {if (document.getElementById("F993430246327ZS4")) {document.getElementById("F993430246327ZS4").focus();};});
	  									//	 ]]>
	  									</script></td></tr>
          								<tr>
	  										<td>Password</td>	
	  										<td><input type="password" name="password" /></td>
	  									</tr>
          								<tr>
	  										<td><input value="Log In" type="submit" /></td></tr>
	  						</table>
	  					</form>
	  							
  							<lift:Msgs showAll="true"> 
          					<lift:error_class>error</lift:error_class> 
          				<lift:notice_class>notice</lift:notice_class> 
          				<lift:warning_class>error</lift:warning_class> 
  						</lift:Msgs> 
  						</div>
  						</div>
  
  
  override def signupXhtml(user: User) = {
	  
	  <div class="signUpBox"><div class="signUpHome fontTahoma">
	  <form method="post" action={ S.uri }> 
	  <table>
	  {localForm(user, false, signupFields)}
	   <tr><td><user:submit/></td></tr>
	  </table>	 
	  </form>
	   </div></div>
	  
  }

  
   override def editXhtml(user : User) = <div class="editBox fontTahoma">
  												<div class="editPage">
	  												<form method="post" action={ S.uri }> 
  														<table>
  															{localForm(user, false, editFields)}
	   														<tr><td><user:submit/></td></tr>
	  													</table>	 
	  												</form>
	   											</div></div>
  
  
  override def changePasswordXhtml = <div id="changeBox"><div class="changePage fontTahoma">
  										{ super.changePasswordXhtml }
  									<lift:Msgs showAll="true"> 
          								<lift:error_class>error</lift:error_class> 
          								<lift:notice_class>notice</lift:notice_class> 
          								<lift:warning_class>error</lift:warning_class> 
  									</lift:Msgs> 
  									</div> </div>
  
  
  override def lostPasswordXhtml = <div id="lostBox" class="fontTahoma"><div class="marginTop">
  										{ super.lostPasswordXhtml }
  										<lift:Msgs showAll="true"> 
          									<lift:error_class>error</lift:error_class> 
          									<lift:notice_class>notice</lift:notice_class> 
          									<lift:warning_class>error</lift:warning_class> 
  										</lift:Msgs> 
  									</div></div> 
 
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