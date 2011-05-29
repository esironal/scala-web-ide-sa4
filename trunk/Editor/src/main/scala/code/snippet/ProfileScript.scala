package code
package snippet

import net.liftweb._
import http._
import js._
import JsCmds._
import JE._
import scala.xml._
import code.model.User

class ProfileScript {
	
	
	
	def render = {

		Script(JsRaw("""
				var container = document.getElementById('ProfileContainer');
				var children = document.getElementsByTagName('div');
				var messages = 0;
				for(var i = 0; i <children.length; i++) {
					if(children[i].id == 'message') {
						messages += 1;
					}

				}
				var height = (87*messages);
				var curHeight = parseInt(container.style.height.split('p')[0]);
				var newHeight =  height+curHeight;
				container.style.height = newHeight+'px';

		"""))
		
	}
	
	
	
}


