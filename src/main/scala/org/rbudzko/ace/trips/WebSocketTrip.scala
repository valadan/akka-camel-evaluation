package org.rbudzko.ace.trips

import akka.actor.{ActorSystem, Props}
import akka.camel.CamelExtension
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.ws.{TextMessage, UpgradeToWebsocket}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.ExpectedWebsocketRequestRejection
import akka.stream.ActorMaterializer
import akka.stream.actor.ActorPublisher
import akka.stream.scaladsl.{Sink, Source}
import org.apache.camel.builder.RouteBuilder
import org.rbudzko.ace.ConsumingWs

object WebSocketTrip {
  def buildCamel(implicit system: ActorSystem) = {
    CamelExtension(system).context.addRoutes(new RouteBuilder() {
      override def configure() {
        from("websocket://camel")
          .transform(simple("Responding: ${body}"))
          .to("websocket://camel")
      }
    })
  }

  def buildAkka(implicit system: ActorSystem, materializer: ActorMaterializer) = {
    Http().bindAndHandle(buildRoute, "localhost", 9393)
  }

  private def buildRoute(implicit system: ActorSystem) = path("akka") {
    get {
      val responder = system.actorOf(Props(classOf[ConsumingWs]))
      optionalHeaderValueByType[UpgradeToWebsocket]() {
        case Some(upgrade) =>
          complete(
            upgrade.handleMessagesWithSinkSource(
              Sink.foreach(responder ! _),
              Source(ActorPublisher[TextMessage](responder))))
        case None =>
          reject(ExpectedWebsocketRequestRejection)
      }
    }
  }
}
