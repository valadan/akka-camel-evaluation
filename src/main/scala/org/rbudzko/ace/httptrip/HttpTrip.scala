package org.rbudzko.ace.httptrip

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
        from("vm:googler")
          .enrich("vm:google-call")
          .choice()
          .when(header(Exchange.HTTP_RESPONSE_CODE).isEqualTo(200))
          .to("vm:google-response-ok")
          .otherwise()
          .to("vm:google-response-fail")

        from("vm:google-response-ok")
          .unmarshal().json(JsonLibrary.Jackson, classOf[SearchResult])
          .to("vm:googled")

        from("vm:google-response-fail")
          .setBody(constant("Fail - boom boom!"))
          .to("vm:googled")

        from("vm:google-call")
          .setHeader(Exchange.HTTP_QUERY, simple("key=" + apiKey + "&cx=001733240814555448082s:yqsjy6oesoq&q=${body}"))
          .to("https4://www.googleapis.com/customsearch/v1?throwExceptionOnFailure=false")
      }
    })

    system.actorOf(Props[ConsumingWorker])
    system.actorOf(Props[ProducingWorker])
  }
}

@JsonIgnoreProperties(ignoreUnknown = true)
case class SearchResult(@JsonProperty("items") items: util.HashSet[SearchItem])

@JsonIgnoreProperties(ignoreUnknown = true)
case class SearchItem(@JsonProperty("link") link: String)

