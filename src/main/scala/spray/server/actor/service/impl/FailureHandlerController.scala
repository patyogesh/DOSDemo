package main.scala.spray.server.actor.service.impl

import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.Terminated
import akka.actor.Props
import spray.can.Http
import akka.io.IO
import scala.collection.mutable.Map
import akka.actor.ActorSystem

class FailureHandlerController(name: String, localAddress: String, localAkkaMessagePort: Int, akkaServerAddress: String, akkaServerPort: Int, followers: Array[Int], requestMap: Map[String, ActorRef], handlerBindingPort: Int)(implicit val system: ActorSystem) extends Actor {
  
  var  handler: ActorRef = context.system.actorOf(Props(new RequestListenerService(name, localAddress, localAkkaMessagePort, akkaServerAddress, akkaServerPort, followers, requestMap)), name = name)
  context watch handler
  IO(Http) ! Http.Bind(handler, interface = localAddress, port = handlerBindingPort)
  
  def receive = {
    case Terminated(a) =>
      val handler: ActorRef = context.system.actorOf(Props(new RequestListenerService(name, localAddress, localAkkaMessagePort, akkaServerAddress, akkaServerPort, followers, requestMap)), name = name)
      IO(Http) ! Http.Bind(handler, interface = localAddress, port = handlerBindingPort)
    case a =>
      println("Unknown message received at FailureHandler Controller on spray server : " + a.toString)
  }
}