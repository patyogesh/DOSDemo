package main.scala.routing.server.main

import akka.actor.ActorSystem
import spray.routing.SimpleRoutingApp
import scala.concurrent.duration.Duration
import spray.routing.HttpService
import spray.routing.authentication.BasicAuth
import spray.routing.directives.CachingDirectives._
import spray.httpx.encoding._
import spray.http.MediaTypes
import main.scala.common.Constants
import java.util.concurrent.ConcurrentHashMap
import scala.collection.concurrent
import akka.actor.ActorRef
import scala.collection.convert.decorateAsScala.mapAsScalaConcurrentMapConverter
import main.scala.common.RegisterUser

object Main extends App with SimpleRoutingApp {

  val requestMap: concurrent.Map[String, ActorRef] = new ConcurrentHashMap().asScala
  val constants = new Constants()
  val akkaServerIP: String = args(0)
  val akkaServerPath = "akka.tcp://AkkaServer@" + akkaServerIP + ":" + constants.AKKA_SERVER_PORT + "/user/"
  //val simpleCache = routeCache(maxCapacity = 1000, timeToIdle = Duration("30 min"))
  val localAddress: String = java.net.InetAddress.getLocalHost.getHostAddress()
  val routingServerPort: Int = 8090
  implicit val system = ActorSystem("RoutingServer")

  val list: List[Int] = List()

  startServer(interface = localAddress, port = routingServerPort) {
    get {
      path("hello") {
        println("Request")
        complete {
          "Done"
        }
      }
    } ~
      get {
        path("hello" / IntNumber) { index =>
          complete {
            "Hello" + index
          }
        }
      } ~
      post {
        path("registeruser" / Segment) { userName =>
          complete {
            var done = false
            var uuid: String = ""
            while (!done) {
              uuid = java.util.UUID.randomUUID().toString()
              if (requestMap.get(uuid) == None) {
                //requestMap += uuid -> sender
                done = true
              }
            }
            val remote = system.actorSelection(akkaServerPath + "UserRegistrationRouter")
            remote ! RegisterUser(uuid, userName, "")
            "Done"
          }
        }
      }
    /*get{
      path("hello" / "give"){
        respondWithMediaType(MediaTypes.'application/json'){
          complete{
            "List"
          }
        }
      }
    }*/
  }

  /*startServer(interface = localAddress , port = routingServerPort) {
    path("registeruser" / ) {
      get {
        complete {
          <h1>Say hello to spray</h1>
        }
      }
    }
  }*/

  /*path("statuses"/"user_timeline"){
parameters("id"){id =>
complete{
var future = remote ? UpdateUserTimeline(id)
var userTweet = Await.result(future, timeout.duration).asInstanceOf[UserTimeline]
JsonUtil.toJson(userTweet.timeline)
}
}
}
}
 */

}