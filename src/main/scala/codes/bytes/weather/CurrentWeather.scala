package codes.bytes.weather

import com.github.nscala_time.time.Imports._

/*
 Simple ADT to represent our "Description" of the temperature
 */
sealed trait TemperatureDescription

object TemperatureDescription {
  object Cold extends TemperatureDescription

  object Moderate extends TemperatureDescription

  object Hot extends TemperatureDescription
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

  case class Clear(description: String) extends WeatherCondition

  case class Cloudy(description: String) extends WeatherCondition
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

object WeatherAlert {
/*  def fromEpochs(event: String, description: String, source: String, start: Long, end: Long, tags: Vector[String]): WeatherAlert = {
    val startDT = new DateTime(start * 1000L)
    val endDT = new DateTime(start * 1000L)
    new WeatherAlert(event, description, source, startDT, endDT, tags)
  } */
}

/*
Core Response Object to describe current weather
 */
case class CurrentWeather(weatherCondition: WeatherCondition,
                          temperatureDescription: TemperatureDescription,
                          activeAlert: Boolean,
                          alerts: Vector[WeatherAlert])
