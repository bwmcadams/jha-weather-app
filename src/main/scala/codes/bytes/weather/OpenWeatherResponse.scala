package codes.bytes.weather

case class OpenWeatherResponse(
                                lat: Double,
                                lon: Double,
                                timezone: String,
                                timezoneOffset: Option[Double],
                                current: ResponseCurrentWeather,
                              )

// Leaving off hourly and daily as our API call specifically asks to skip them
case class ResponseCurrentWeather(
                                 dt: Long, // these longs would all be converted to Java DateTime objects in a more complete Impl
                                 sunrise: Long,
                                 sunset: Long,
                                 temp: Double,
                                 feels_like: Double,
                                 pressure: Long,
                                 humidity: Long,
                                 dew_point: Double,
                                 uvi: Double,
                                 clouds: Long,
                                 visibility: Long,
                                 wind_speed: Double,
                                 wind_deg: Long,
                                 weather: Vector[ResponseWeatherCondition],
                                 snow: Option[ResponseSnowInfo],
                                 rain: Option[ResponseRainInfo],
                                 minutely: Option[ResponseMinutely],
                                 alerts: Option[Vector[WeatherAlert]]
                                )

case class ResponseWeatherCondition(
                                   id: Long,
                                   main: String,
                                   description: String,
                                   icon: String
                                   )

case class ResponseRainInfo(_1h: Double)

case class ResponseSnowInfo(_1h: Double)

case class ResponseMinutely(dt: Long, precipitation: Double)

