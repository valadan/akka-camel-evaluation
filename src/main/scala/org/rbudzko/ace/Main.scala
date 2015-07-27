package org.rbudzko.ace

import akka.actor.ActorSystem
import akka.pattern.ask
import akka.util.Timeout
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._

object Main extends App {
  implicit val log = LoggerFactory.getLogger(classOf[App])
  implicit val system = ActorSystem.create("main-system")
  implicit val timeout = Timeout(5 seconds)

  system.scheduler.scheduleOnce(2 second, () => {
    args(0) match {
      case apiKey: String =>
        val roundTripResult = RoundTrip.build ? "Welcome!"
        roundTripResult map ($ => log.info("Round trip responded with {}.", $))

        val httpTripResult = HttpTrip.build(apiKey) ? "Robert Budźko"
        httpTripResult map ($ => log.info("Http trip responded with {}.", $))

        Future.sequence(List(roundTripResult, httpTripResult)) andThen {
          case responses: Any =>
            log.info("All futures finished - terminating. Responses: {}", responses)
            system.terminate()
        }

        log.info("All requested.")
      case _ =>
        log.error("Google API Key needs to be passed as a first Main argument. Exit!")
    }
  })

  implicit def runnableToFunction(f: () => Unit): Runnable with Object = new Runnable() {
    override def run(): Unit = f()
  }
}