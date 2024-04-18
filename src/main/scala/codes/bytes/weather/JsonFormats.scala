package codes.bytes.weather

import spray.json._

object JsonFormats extends DefaultJsonProtocol {

  implicit object WeatherConditionJsonFormat extends RootJsonFormat[WeatherCondition] {
    def write(wc: WeatherCondition) =
      JsString(wc.description)

    def read(value: JsValue) = ???

  }

  implicit object TemperatureDescriptionJsonFormat extends RootJsonFormat[TemperatureDescription] {
    // this is an embarassingly hacky approach, I know
    // todo - is there a way to just quickly translate the object name?
    def write(wc: TemperatureDescription) = wc match { // this is exhaustive
      case TemperatureDescription.Cold => JsString("Cold")
      case TemperatureDescription.Moderate => JsString("Moderate")
      case TemperatureDescription.Hot => JsString("Hot")
    }

    def read(value: JsValue) = ???

  }

  implicit val responseWeatherCondition: RootJsonFormat[ResponseWeatherCondition] = jsonFormat4(ResponseWeatherCondition.apply)
  implicit val responseRainInfo: RootJsonFormat[ResponseRainInfo] = jsonFormat1(ResponseRainInfo.apply)
  implicit val responseSnowInfo: RootJsonFormat[ResponseSnowInfo] = jsonFormat1(ResponseSnowInfo.apply)
  implicit val responseMinutely: RootJsonFormat[ResponseMinutely] = jsonFormat2(ResponseMinutely.apply)
  implicit val weatherAlert: RootJsonFormat[WeatherAlert] = jsonFormat6(WeatherAlert.apply)
  implicit val responseCurrentWeather: RootJsonFormat[ResponseCurrentWeather] = jsonFormat18(ResponseCurrentWeather.apply)
  implicit val apiResponseFormat: RootJsonFormat[OpenWeatherResponse] = jsonFormat5(OpenWeatherResponse.apply)
  implicit val currentWeather: RootJsonFormat[CurrentWeather] = jsonFormat4(CurrentWeather.apply)


}
