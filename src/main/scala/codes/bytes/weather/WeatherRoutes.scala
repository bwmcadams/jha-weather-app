package codes.bytes.weather

import codes.bytes.weather.JsonFormats._
import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.http.scaladsl.Http
import org.apache.pekko.http.scaladsl.client.RequestBuilding.Get
import org.apache.pekko.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import org.apache.pekko.http.scaladsl.model.StatusCodes
import org.apache.pekko.http.scaladsl.server.Directives._
import org.apache.pekko.http.scaladsl.server.Route
import org.apache.pekko.http.scaladsl.unmarshalling.Unmarshal
import org.apache.pekko.pattern.CircuitBreaker
import org.apache.pekko.util.Timeout

import scala.concurrent.Future
import scala.concurrent.duration._
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
          // this should help if the openweather API goes down, instead of straight up failures
          val breaker = new CircuitBreaker(system.scheduler, maxFailures = 1, callTimeout = 5.seconds, resetTimeout)
          // check latitude and longitude are in proper ranges, if they are then execute the requesdt
          if (req.lat < -90 || req.lat > 90) complete(StatusCodes.BadRequest, "{\"error\": \"Latitude out of range. Min -90 Max +90\"}")
          else if (req.lon < -180 || req.lon > 180) complete(StatusCodes.BadRequest, "{\"error\": \"Longitude out of range. Min -180 Max +180\"}")
          else onCompleteWithBreaker(breaker)(processWeatherRequest(req)) {
            case Success(resp) =>
              complete(StatusCodes.OK, resp)
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
    val apiUrl = s"https://api.openweathermap.org/data/2.5/onecall?lat=${req.lat}&lon=${req.lat}&exclude=hourly,daily&appid=$apiKey&units=imperial"
    system.log.debug("API URL to be called: {}", apiUrl)
    val weatherApiReq =
      Get(apiUrl)
    val responseFuture = {
      Http()(system)
        .singleRequest(weatherApiReq)
    } flatMap { response =>
      // I got a bit stuck here trying to remember how to properly pass the materializer
      // this is a lazy fix, admittedly
      implicit val _system: ActorSystem = system
      Unmarshal(response.entity).to[OpenWeatherResponse].map { weatherResponse =>
        system.log.debug("Parsed an OpenWeatherResponse from JSON: {}", weatherResponse)

        CurrentWeather.fromAPIResponse(weatherResponse)
      }

    }
    responseFuture
  }

}

