package code
package model

import net.liftweb.mapper._

object UserProject extends UserProject with MetaMapper[UserProject]

class UserProject extends Mapper[UserProject] {
    def getSingleton = UserProject

    object user extends LongMappedMapper(this, User)
    
    object project extends LongMappedMapper(this, Project)
}