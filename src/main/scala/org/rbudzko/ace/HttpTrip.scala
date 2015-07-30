package org.rbudzko.ace

import java.util

import akka.actor.{ActorSystem, Props}
import akka.camel.CamelExtension
import akka.stream.ActorMaterializer
import akka.stream.actor.ActorPublisher
import akka.stream.scaladsl.{Sink, Source}
import org.apache.camel.Exchange
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.model.dataformat.JsonLibrary
import org.codehaus.jackson.annotate.{JsonIgnoreProperties, JsonProperty}
import org.slf4j.LoggerFactory

object HttpTrip {

  implicit val logger = LoggerFactory.getLogger(HttpTrip.getClass)

  def build(apiKey: String)(implicit system: ActorSystem) = {
    implicit val materializer = ActorMaterializer.create(system)

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

    Source(ActorPublisher[SearchItem](system.actorOf(Props(classOf[ConsumingPublisher], "direct-vm://googled"))))
      .to(Sink.foreach(logger.info("Sank item {}.", _)))
      .run()

    system.actorOf(Props(classOf[ProducingWorker], "direct-vm://googler"))
  }
}

@JsonIgnoreProperties(ignoreUnknown = true)
case class SearchResult(@JsonProperty("items") items: util.HashSet[SearchItem])

@JsonIgnoreProperties(ignoreUnknown = true)
case class SearchItem(@JsonProperty("link") link: String)

