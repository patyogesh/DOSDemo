package main.scala.routing.server.main

import akka.actor.ActorSystem
import spray.routing.SimpleRoutingApp
import scala.concurrent.duration.Duration
import spray.routing.HttpService
import spray.routing.authentication.BasicAuth
import spray.routing.directives.CachingDirectives._
import spray.httpx.encoding._
import spray.http.MediaTypes
import main.scala.common._
import java.util.concurrent.ConcurrentHashMap
import scala.collection.concurrent
import akka.actor.ActorRef
import scala.collection.convert.decorateAsScala.mapAsScalaConcurrentMapConverter
import main.scala.common.RegisterUser
import com.typesafe.config.ConfigFactory

object Main extends App with SimpleRoutingApp {

  val requestMap: concurrent.Map[String, ActorRef] = new ConcurrentHashMap().asScala
  val constants = new Constants()
  val akkaServerIP: String = args(0)
  val akkaServerPath = "akka.tcp://AkkaServer@" + akkaServerIP + ":" + constants.AKKA_SERVER_PORT + "/user/"
  //val simpleCache = routeCache(maxCapacity = 1000, timeToIdle = Duration("30 min"))
  val localAddress: String = java.net.InetAddress.getLocalHost.getHostAddress()
  val routingServerPort: Int = 8090

  val configString = """akka {
  actor {
    provider = "akka.remote.RemoteActorRefProvider"
  }
  remote {
    enabled-transports = ["akka.remote.netty.tcp"]
    netty.tcp {
      hostname = """ + localAddress + """
      port = """ + (routingServerPort + 1).toInt + """
    }
 }
}"""

  val configuration = ConfigFactory.parseString(configString)
  implicit val system = ActorSystem("RoutingServer", ConfigFactory.load(configuration))

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
        path("hello" / Segment) { index =>
          complete {
            println(index)
            "Hello" + index
          }
        }
      } ~
      //Register user
      //http://172.16.110.167:8090/registeruser/bhavnesh
      post {
        path("registeruser" / Segment) { userName =>
          var uuid = java.util.UUID.randomUUID().toString()
          val remote = system.actorSelection(akkaServerPath + "UserRegistrationRouter")
          remote ! RegisterUser(uuid, userName, "")
          complete {
            println("Done")
            "Done"
          }
        }
      } ~
      //Register multiple users
      //http://172.16.110.167:8090/userregistration?ip=1.2.3.4&client=100&samplesize=10&peakactorname=PeakActor&peakactorfollowerscount=10
      post {
        path("userregistration") {
          parameters("ip".as[String], "clients".as[Int], "samplesize".as[Int], "peakactorname".as[String], "peakactorfollowerscount".as[Int]) { (ip, clients, samplesize, peakactorname, peakactorfollowerscount) =>
            var uuid = java.util.UUID.randomUUID().toString()
            val remote = system.actorSelection(akkaServerPath + "UserRegistrationRouter")
            remote ! RegisterUsers(uuid, ip, clients, "", constants.followers, samplesize, peakactorname, peakactorfollowerscount)
            complete {
              "Done"
            }
          }
        }
      } ~
      //Post Tweet Update
      //http://172.16.110.167:8090/tweet/update/bhavnesh?tweet=abcd
      post {
        path("tweet" / "update" / Segment) { username =>
          parameters("tweet".as[String]) { (tweet) =>
            var uuid = java.util.UUID.randomUUID().toString()
            val endPoint = "postupdate"
            val remote = system.actorSelection(akkaServerPath + "UserRegistrationRouter")
            remote ! new AkkaRequest(uuid, "", endPoint, username, "", tweet)
            complete {
              "Done"
            }
          }
        }
      }
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