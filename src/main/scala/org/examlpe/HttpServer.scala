package org.examlpe

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http

import scala.io.StdIn

object HttpServer {
  def main(args: Array[String]): Unit = {


    implicit val system = ActorSystem(Behaviors.empty, "MySystem")
    implicit val executionContext = system.executionContext

    val routeInstance = new JsonModel
    val route = routeInstance.route

    val bindingFuture = Http().newServerAt("localhost", 8080).bind(route)

    println(s"Server now online. Please navigate to http://localhost:8080/.....\nPress RETURN to stop...")
    StdIn.readLine()
    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
  }
}
