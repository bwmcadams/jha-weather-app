package codes.bytes.weather

import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import JsonFormats._
import org.apache.pekko.util.Timeout
import org.apache.pekko.http.scaladsl.server.Directives._
import org.apache.pekko.http.scaladsl.model.StatusCodes
import org.apache.pekko.http.scaladsl.server.Route
import org.apache.pekko.pattern.CircuitBreaker

import scala.concurrent.Future
import scala.concurrent.duration._


class WeatherRoutes(system: ActorSystem) {
  private implicit val timeout: Timeout =
    Timeout.create(system.settings.config.getDuration("weather.routes.ask-timeout"))

  import system.dispatcher
  val weatherRoutes: Route =
    path("weather") {
      get {
        parameters("lon".as[Double], "lat".as[Double]).as(WeatherRequest) { (req: WeatherRequest) =>
          val resetTimeout = 1.second
          val breaker = new CircuitBreaker(
            system.scheduler,
            maxFailures = 1,
            callTimeout = 5.seconds,
            resetTimeout)

          onCompleteWithBreaker(breaker)(processWeatherRequest(req))
        }
      }
    }

  private def processWeatherRequest(req: WeatherRequest): Future[CurrentWeather] = {
    system.log.debug("Got a WeatherRequest {}", req)

  }

}

case class WeatherRequest(lon: Double, lat: Double)
