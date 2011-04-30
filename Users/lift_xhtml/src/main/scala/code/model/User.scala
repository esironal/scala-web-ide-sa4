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
  // define the order fields will appear in forms and output
  override def fieldOrder = List(id, accountID, firstName, lastName, email,
  locale, timezone, password, skypeID, gender, personalImageURL, textArea)

  override def signupFields = List(accountID, firstName, lastName, password, email, gender, skypeID)	
  override def editFields = List(firstName, lastName, password, gender, skypeID, personalImageURL)	


  // comment this line out to require email validations
  override def skipEmailValidation = true
}

/**
 * An O-R mapped "User" class that includes first name, last name, password and we add a "Personal Essay" to it
 */
class User extends MegaProtoUser[User] {
  	def getSingleton = User // what's the "meta" server
  	
	object accountID extends MappedString(this, 30){
		override def displayName = "Account Name"
		override def validations = valUnique(S.?("user.unique.accountID")) _ :: super.validations
	}

	object skypeID extends MappedString(this, 30) {
		override def displayName = "Skype ID"
	}
	
	object gender extends MappedGender(this){
	}
	
	object personalImageURL extends MappedString(this, 200){
		override def displayName = "Personal Image"
		override def defaultValue = "/images/img.png" 
	}

  // define an additional field for a personal essay
  object textArea extends MappedTextarea(this, 2048) {
    override def textareaRows  = 3
    override def textareaCols = 50
    override def displayName = "Personal Essay"
  }
}
