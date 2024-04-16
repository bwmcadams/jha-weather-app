package codes.bytes.weather

import com.github.nscala_time.time.Imports._

/*
 Simple ADT to represent our "Description" of the temperature
 */
sealed trait TemperatureDescription
object Cold extends TemperatureDescription
object Moderate extends TemperatureDescription
object Hot extends TemperatureDescription

/*
ADT to represent the "broad" Weather Description (rain, thunderstorm, snow, etc)
Includes the more detailed description
 */
sealed trait WeatherCondition {
  val description: String
}
case class Thunderstorm(description: String) extends WeatherCondition
case class Drizzle(description: String) extends WeatherCondition
case class Rain(description: String) extends WeatherCondition
case class Snow(description: String) extends WeatherCondition
case class Clear(description: String) extends WeatherCondition
case class Cloudy(description: String) extends WeatherCondition


/*
Weather Alerts
These make up a list on CurrentWeather
 */
case class WeatherAlert(event: String,
                        description: String,
                        source: String /* sender name */,
                        start: DateTime,
                        end: DateTime,
                        tags: Vector[String])

/*
Core Response Object to describe current weather
 */
case class CurrentWeather(weatherCondition: WeatherCondition,
                          temperatureDescription: TemperatureDescription,
                          activeAlert: Boolean,
                          alerts: Vector[WeatherAlert])
