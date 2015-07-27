package org.rbudzko.ace

import java.util

import akka.actor.{ActorSystem, Props}
import akka.camel.CamelExtension
import org.apache.camel.Exchange
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.model.dataformat.JsonLibrary
import org.codehaus.jackson.annotate.{JsonIgnoreProperties, JsonProperty}

object HttpTrip {
  def build(apiKey: String)(implicit system: ActorSystem) = {
    CamelExtension(system).context.addRoutes(new RouteBuilder() {
      override def configure() {
        from("direct-vm://googler")
          .enrich("direct-vm://google-call")
          .choice()
          .when(header(Exchange.HTTP_RESPONSE_CODE).isEqualTo(200))
          .unmarshal().json(JsonLibrary.Jackson, classOf[SearchResult])
          .to("direct-vm://googled")
          .otherwise()
          .setBody(constant("Fail - boom boom!"))
          .to("direct-vm://googled")

        from("direct-vm://google-call")
          .setHeader(Exchange.HTTP_QUERY, simple("key=" + apiKey + "&cx=001733240814555448082:yqsjy6oesoq&q=${body}"))
          .to("https4://www.googleapis.com/customsearch/v1?throwExceptionOnFailure=false")
      }
    })

    system.actorOf(Props(classOf[ConsumingWorker], "direct-vm://googled"))
    system.actorOf(Props(classOf[ProducingWorker], "direct-vm://googler"))
  }
}

@JsonIgnoreProperties(ignoreUnknown = true)
case class SearchResult(@JsonProperty("items") items: util.HashSet[SearchItem])

@JsonIgnoreProperties(ignoreUnknown = true)
case class SearchItem(@JsonProperty("link") link: String)

