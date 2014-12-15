package main.scala.common

import akka.util.Timeout
import scala.concurrent.duration._

//#Contains various constants used in the project.  
class Constants {
  val AKKA_SERVER_PORT: Int = 7171
  val AKKA_CLIENT_PORT: Int = 7172
  val SPRAY_SERVER_PORT_FOR_HTTP_MESSAGES: Int = 7173
  val SPRAY_SERVER_PORT_FOR_AKKA_MESSAGES: Int = 7174
  val SPRAY_CLIENT_PORT_FOR_HTTP_MESSAGES: Int = 7175
  val SPRAY_CLIENT_PORT_FOR_AKKA_MESSAGES: Int = 7176
  val UPDATE_TIMEOUT: Int = 2000

  val followers = Array(8, 7, 7, 5, 5, 3, 3, 1, 1, 1)
  
  //Spray Timeout
  val TIMEOUT: Timeout = 10.seconds 
}
