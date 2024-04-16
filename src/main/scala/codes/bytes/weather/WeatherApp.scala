package codes.bytes.weather

import org.apache.pekko
import pekko.actor.ActorSystem
import pekko.http.scaladsl.Http
import pekko.http.scaladsl.server.Route
import pekko.actor.ActorSystem
import pekko.http.scaladsl.Http
import pekko.stream.scaladsl._

import scala.util.Failure
import scala.util.Success

object WeatherApp {
  private def startHttpServer(routes: Route)(implicit system: ActorSystem): Unit = {
    val futureBinding = Http()
      .newServerAt("localhost", 8080)
      .bind(routes)

    futureBinding.onComplete {
      case Success(binding) =>
        val address = binding.localAddress
        system.log.info("Server online at http://{}:{}/", address.getHostString, address.getPort)
      case Failure(ex) =>
        system.log.error("Failed to bind HTTP endpoint, terminating system", ex)
        system.terminate()
    }(system.dispatcher /* execution context*/)
  }
  :throws
  def main(args: Array[String]): Unit = {
      val system = ActorSystem("WeatherHTTPServer")
      val routes = new WeatherRoutes(system)
      startHttpServer(routes.weatherRoutes)(system)

  }
}
//#main-class
