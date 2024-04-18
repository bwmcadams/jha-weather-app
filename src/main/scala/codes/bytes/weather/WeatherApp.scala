package codes.bytes.weather

import org.apache.pekko
import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.http.scaladsl.Http
import org.apache.pekko.http.scaladsl.server.Route

import scala.util.{Failure, Success}

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

  def main(args: Array[String]): Unit = {
    // Start up an ActorSystem to use with PekkoHTTP
    val system = ActorSystem("WeatherHTTPServer")
    val routes = new WeatherRoutes(system)
    startHttpServer(routes.weatherRoutes)(system)
  }
}
