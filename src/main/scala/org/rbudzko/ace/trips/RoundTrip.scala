package org.rbudzko.ace.trips

import akka.actor.{ActorSystem, Props}
import akka.camel.CamelExtension
import org.apache.camel.builder.RouteBuilder
import org.rbudzko.ace.{ConsumingWorker, ProducingWorker}

object RoundTrip {
  def build(implicit system: ActorSystem) = {
    CamelExtension(system).context.addRoutes(new RouteBuilder() {
      override def configure() {
        from("direct-vm://input").to("direct-vm://output")
      }
    })

    system.actorOf(Props(classOf[ConsumingWorker], "direct-vm://output"))
    system.actorOf(Props(classOf[ProducingWorker], "direct-vm://input"))
  }
}
