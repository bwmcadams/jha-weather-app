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
import org.apache.pekko.http.scaladsl.Http
import org.apache.pekko.http.scaladsl.model._
import org.apache.pekko.http.scaladsl.client.RequestBuilding.Get
import org.apache.pekko.http.scaladsl.unmarshalling.Unmarshal
import spray.json._

import scala.util.{Failure, Success}


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
          onCompleteWithBreaker(breaker)(processWeatherRequest(req)) {
            case Success(resp) =>
              complete(StatusCodes.OK, "foobar")
            case Failure(exception) =>
              // todo - a few different fail scenarios
              complete(StatusCodes.InternalServerError, s"Error: ${exception}")
          }
        }
      }
    }

  private def processWeatherRequest(req: WeatherRequest): Future[CurrentWeather] = {
    system.log.info("Got a WeatherRequest {}", req)

    val apiKey = system.settings.config.getString("weather.openweather.api-key")
    // todo - check lat and lon are in range
    val apiUrl = s"https://api.openweathermap.org/data/2.5/onecall?lat=${req.lat}&lon=${req.lat}&exclude=hourly,daily&appid=$apiKey&units=imperial"
    system.log.info("API URL to be called: {}", apiUrl)
    val weatherApiReq =
      Get(apiUrl)
    val responseFuture = {
      Http()(system)
        .singleRequest(weatherApiReq)
    } flatMap { response =>
      // I got a bit stuck here trying to remember how to properly pass the materializer
      // this is a lazy fix, admittedly
      implicit val _system: ActorSystem = system
      system.log.info("{}", response.entity)
      Unmarshal(response.entity).to[OpenWeatherResponse].map { weatherResponse =>
          system.log.info("Raw JSON from weather API: {}", weatherResponse)
        
          system.log.info("Parsed an OpenWeatherResponse from JSON: {}", weatherResponse)

          CurrentWeather(
            Vector(WeatherCondition.Thunderstorm("my hovercraft is full of eels")),
            TemperatureDescription.Cold,
            activeAlerts =  true,
            alerts = Vector.empty[WeatherAlert]
          )
      }

    }
    responseFuture
  }

}

case class WeatherRequest(lon: Double, lat: Double)
