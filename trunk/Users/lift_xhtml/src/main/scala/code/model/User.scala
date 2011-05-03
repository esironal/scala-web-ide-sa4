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


  override def skipEmailValidation = true
}

class User extends MegaProtoUser[User] {
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
}