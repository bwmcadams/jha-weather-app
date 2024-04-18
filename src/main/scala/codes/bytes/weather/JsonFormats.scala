package codes.bytes.weather

import spray.json._
import com.github.nscala_time.time.Imports._
import org.joda.time.format.{DateTimeFormatter, ISODateTimeFormat}

object JsonFormats extends DefaultJsonProtocol {
  // import the default encoders for primitive types (Int, String, Lists etc)
  import DefaultJsonProtocol._

/*  // it's been awhile since I dealt with Joda Time conversions in Spray JSON
  // so this is a bit of a hacky mess (via StackOverflow)
  implicit object DateTimeJsonFormat extends RootJsonFormat[DateTime] {

    private val parserISO : DateTimeFormatter = ISODateTimeFormat.dateTimeNoMillis();

    override def write(obj: DateTime) = JsString(parserISO.print(obj))

    override def read(json: JsValue) : DateTime = json match {
      case JsString(s) => parserISO.parseDateTime(s)
      case _ => throw new DeserializationException("Error info you want here ...")
    }
  }
*/
  implicit val responseWeatherCondition: RootJsonFormat[ResponseWeatherCondition] = jsonFormat4(ResponseWeatherCondition.apply)
  implicit val responseRainInfo: RootJsonFormat[ResponseRainInfo] = jsonFormat1(ResponseRainInfo.apply)
  implicit val responseSnowInfo: RootJsonFormat[ResponseSnowInfo] = jsonFormat1(ResponseSnowInfo.apply)
  implicit val responseMinutely: RootJsonFormat[ResponseMinutely] = jsonFormat2(ResponseMinutely.apply)
  implicit val weatherAlert: RootJsonFormat[WeatherAlert] = jsonFormat6(WeatherAlert.apply)
  implicit val responseCurrentWeather: RootJsonFormat[ResponseCurrentWeather] = jsonFormat18(ResponseCurrentWeather.apply)
  implicit val apiResponseFormat: RootJsonFormat[OpenWeatherResponse] = jsonFormat5(OpenWeatherResponse.apply)
  implicit val weatherThunderstorm: RootJsonFormat[WeatherCondition.Thunderstorm] = jsonFormat1(WeatherCondition.Thunderstorm)
  implicit val weatherDrizzle: RootJsonFormat[WeatherCondition.Drizzle] = jsonFormat1(WeatherCondition.Drizzle)
  implicit val weatherRain: RootJsonFormat[WeatherCondition.Rain] = jsonFormat1(WeatherCondition.Rain)
  implicit val weatherSnow: RootJsonFormat[WeatherCondition.Snow] = jsonFormat1(WeatherCondition.Snow)
  implicit val weatherClear: RootJsonFormat[WeatherCondition.Clear] = jsonFormat1(WeatherCondition.Clear)
  implicit val weatherCloudy: RootJsonFormat[WeatherCondition.Cloudy] = jsonFormat1(WeatherCondition.Cloudy)
  implicit val currentWeather: RootJsonFormat[CurrentWeather] = jsonFormat4(CurrentWeather.apply)


}
