package org.rbudzko.ace.roundtrip

import akka.actor.{ActorSystem, Props}
import akka.camel.CamelExtension
import org.apache.camel.builder.RouteBuilder

object RoundTrip {
  def build(implicit system: ActorSystem) = {
    CamelExtension(system).context.addRoutes(new RouteBuilder() {
      override def configure() {
        from("direct-vm://input").to("direct-vm://output")
      }
    })

    system.actorOf(Props[ConsumingWorker])
    system.actorOf(Props[ProducingWorker])
  }
}
