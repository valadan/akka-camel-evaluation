package org.rbudzko.ace.trips

import akka.actor.ActorSystem
import akka.camel.CamelExtension
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Source}
import org.apache.camel.builder.RouteBuilder

object WebSocketTrip {
  def buildCamel(implicit system: ActorSystem) = {
    CamelExtension(system).context.addRoutes(new RouteBuilder() {
      override def configure() {
        from("websocket://echo")
          .transform(simple("Responding: ${body}"))
          .to("websocket://echo")
      }
    })
  }

  val greeterWebsocketService = Flow[Message].collect {
    case tm: TextMessage =>
      TextMessage(Source.single("Hello ") ++ tm.textStream)
  }

  val route = path("ws-greeter") {
    get {
      handleWebsocketMessages(greeterWebsocketService)
    }
  }

  def buildAkka(implicit system: ActorSystem, materializer: ActorMaterializer) = {
    Http().bindAndHandle(route, "localhost", 9393)
  }
}
