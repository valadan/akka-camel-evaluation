package org.rbudzko.ace

import akka.actor.ActorSystem
import akka.pattern.ask
import akka.util.Timeout
import org.rbudzko.ace.httptrip.HttpTrip
import org.rbudzko.ace.roundtrip.RoundTrip
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

object Main extends App {
  implicit val log = LoggerFactory.getLogger(classOf[App])
  implicit val system = ActorSystem.create("main-system")
  implicit val timeout = Timeout(5 second)

  args(0) match {
    case apiKey: String =>
      RoundTrip.build ? "Welcome!" map ($ => log.info("Round trip responded with {}.", $))
      HttpTrip.build(apiKey) ? "Robert Budźko" map ($ => log.info("Http trip responded with {}.", $))
    case _ => log.error("Google API Key needs to be passed as a first Main argument. Exit!")
  }
}