package codes.bytes.weather

case class WeatherRequest(lon: Double, lat: Double)
/*
 Simple ADT to represent our "Description" of the temperature
 */
sealed trait TemperatureDescription

object TemperatureDescription {
  object Cold extends TemperatureDescription

  object Moderate extends TemperatureDescription

  object Hot extends TemperatureDescription

  // this assumes imperial units
  def apply(temp: Double) = {
    if (temp > 85.0) TemperatureDescription.Hot
    else if (temp < 55.0) TemperatureDescription.Cold
    else TemperatureDescription.Moderate
  }
}

/*
ADT to represent the "broad" Weather Description (rain, thunderstorm, snow, etc)
Includes the more detailed description
 */
sealed trait WeatherCondition {
  val description: String
}

object WeatherCondition {
  case class Thunderstorm(description: String) extends WeatherCondition

  case class Drizzle(description: String) extends WeatherCondition

  case class Rain(description: String) extends WeatherCondition

  case class Snow(description: String) extends WeatherCondition

  case class Atmospheric(description: String) extends WeatherCondition

  case class Clear(description: String) extends WeatherCondition

  case class Cloudy(description: String) extends WeatherCondition

  // parse out a instance of Weather Condition based on the more complete detail from OW
  def fromAPIResponse(responseWC: Vector[ResponseWeatherCondition]): Vector[WeatherCondition] = {
    val weatherList = for {
      w <- responseWC
    } yield {
      // todo - i think this could be cleaner…
      if (w.id >= 200 && w.id <= 300) { //Thunderstorm group
        WeatherCondition.Thunderstorm(w.description)
      } else if (w.id >= 300 && w.id < 400) { // Drizzle group
        WeatherCondition.Drizzle(w.description)
      } else if (w.id >= 500 && w.id < 600) { // Rain group
        WeatherCondition.Rain(w.description)
      } else if (w.id >= 600 && w.id < 700) { // Snow group
        WeatherCondition.Snow(w.description)
      } else if (w.id >= 700 && w.id < 800) { // Atmo group; this has a lot of subtypes we didn't explicitly model in case clases
        WeatherCondition.Atmospheric(w.description)
      } else if (w.id == 800) { // Clear skies
        WeatherCondition.Clear(w.description)
      } else if (w.id > 800 && w.id < 900) {
        WeatherCondition.Cloudy(w.description)
      } else {
        throw new IllegalArgumentException(s"Unknown response current weather object: $w")
      }
    }
    weatherList
  }
}


/*
Weather Alerts
These make up a list on CurrentWeather
 */
case class WeatherAlert(event: String,
                        description: String,
                        source: String, /* sender name e.g. National Weather Service */
                        start: Long, /* epoch seconds */
                        end: Long, /* epoch seconds */
                        tags: Vector[String])

/*
Core Response Object to describe current weather
 */
case class CurrentWeather(weatherConditions: Vector[WeatherCondition],
                          temperatureDescription: TemperatureDescription,
                          activeAlerts: Boolean,
                          alerts: Vector[WeatherAlert])

object CurrentWeather {
  def fromAPIResponse(weatherResponse: OpenWeatherResponse): CurrentWeather = {
    val responseCW = weatherResponse.current
    val weatherConditions = responseCW.weather
    val convertedWeathers = WeatherCondition.fromAPIResponse(weatherConditions)
    val tempDescription = TemperatureDescription(responseCW.temp)

    val activeAlerts = responseCW.alerts.isDefined

    val alerts = responseCW.alerts match {
      case Some(a) => a
      case None => Vector.empty
    }

    new CurrentWeather(convertedWeathers, tempDescription, activeAlerts, alerts)
  }
}
