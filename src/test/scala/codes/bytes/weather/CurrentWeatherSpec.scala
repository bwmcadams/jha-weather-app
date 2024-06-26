package codes.bytes.weather

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import spray.json._

// kind of a catchall test set that pokes at a few things related to `CurrentWeather`
class CurrentWeatherSpec extends AnyWordSpec with Matchers with ScalaFutures {

  import JsonFormats._

  val shortJsonResponse =
    """
      |{
      |  "lat": 2,
      |  "lon": 2,
      |  "timezone": "Etc/GMT",
      |  "timezone_offset": 0,
      |  "current": {
      |    "dt": 1713460888,
      |    "sunrise": 1713419167,
      |    "sunset": 1713462959,
      |    "temp": 84.79,
      |    "feels_like": 93.74,
      |    "pressure": 1008,
      |    "humidity": 74,
      |    "dew_point": 75.58,
      |    "uvi": 0.42,
      |    "clouds": 75,
      |    "visibility": 10000,
      |    "wind_speed": 10.6,
      |    "wind_deg": 171,
      |    "wind_gust": 10.78,
      |    "weather": [
      |      {
      |        "id": 803,
      |        "main": "Clouds",
      |        "description": "broken clouds",
      |        "icon": "04d"
      |      }
      |    ]
      |  }
      |}
      |""".stripMargin

  val fullJsonResponse =
    """
      |{
      |  "lat": 33.44,
      |  "lon": -94.04,
      |  "timezone": "America/Chicago",
      |  "timezone_offset": -21600,
      |  "current": {
      |    "dt": 1618317040,
      |    "sunrise": 1618282134,
      |    "sunset": 1618333901,
      |    "temp": 284.07,
      |    "feels_like": 282.84,
      |    "pressure": 1019,
      |    "humidity": 62,
      |    "dew_point": 277.08,
      |    "uvi": 0.89,
      |    "clouds": 0,
      |    "visibility": 10000,
      |    "wind_speed": 6,
      |    "wind_deg": 300,
      |    "weather": [
      |      {
      |        "id": 500,
      |        "main": "Rain",
      |        "description": "light rain",
      |        "icon": "10d"
      |      }
      |    ],
      |    "rain": {
      |      "1h": 0.21
      |    }
      |  },
      |    "minutely": [
      |    {
      |      "dt": 1618317060,
      |      "precipitation": 0.205
      |    },
      |    ...
      |  },
      |    "hourly": [
      |    {
      |      "dt": 1618315200,
      |      "temp": 282.58,
      |      "feels_like": 280.4,
      |      "pressure": 1019,
      |      "humidity": 68,
      |      "dew_point": 276.98,
      |      "uvi": 1.4,
      |      "clouds": 19,
      |      "visibility": 306,
      |      "wind_speed": 4.12,
      |      "wind_deg": 296,
      |      "wind_gust": 7.33,
      |      "weather": [
      |        {
      |          "id": 801,
      |          "main": "Clouds",
      |          "description": "few clouds",
      |          "icon": "02d"
      |        }
      |      ],
      |      "pop": 0
      |    },
      |    ...
      |  }
      |    "daily": [
      |    {
      |      "dt": 1618308000,
      |      "sunrise": 1618282134,
      |      "sunset": 1618333901,
      |      "moonrise": 1618284960,
      |      "moonset": 1618339740,
      |      "moon_phase": 0.04,
      |      "temp": {
      |        "day": 279.79,
      |        "min": 275.09,
      |        "max": 284.07,
      |        "night": 275.09,
      |        "eve": 279.21,
      |        "morn": 278.49
      |      },
      |      "feels_like": {
      |        "day": 277.59,
      |        "night": 276.27,
      |        "eve": 276.49,
      |        "morn": 276.27
      |      },
      |      "pressure": 1020,
      |      "humidity": 81,
      |      "dew_point": 276.77,
      |      "wind_speed": 3.06,
      |      "wind_deg": 294,
      |      "weather": [
      |        {
      |          "id": 500,
      |          "main": "Rain",
      |          "description": "light rain",
      |          "icon": "10d"
      |        }
      |      ],
      |      "clouds": 56,
      |      "pop": 0.2,
      |      "rain": 0.62,
      |      "uvi": 1.93
      |    },
      |    ...
      |    },
      |    "alerts": [
      |    {
      |      "sender_name": "NWS Tulsa",
      |      "event": "Heat Advisory",
      |      "start": 1597341600,
      |      "end": 1597366800,
      |      "description": "...HEAT ADVISORY REMAINS IN EFFECT FROM 1 PM THIS AFTERNOON TO\n8 PM CDT THIS EVENING...\n* WHAT...Heat index values of 105 to 109 degrees expected.\n* WHERE...Creek, Okfuskee, Okmulgee, McIntosh, Pittsburg,\nLatimer, Pushmataha, and Choctaw Counties.\n* WHEN...From 1 PM to 8 PM CDT Thursday.\n* IMPACTS...The combination of hot temperatures and high\nhumidity will combine to create a dangerous situation in which\nheat illnesses are possible.",
      |      "tags": [
      |        "Extreme temperature value"
      |        ]
      |    },
      |    ...
      |  ]
      |""".stripMargin


  "Parsing JSON from OpenWeatherMap OneCall" should {
    "successfully parse and return a simple JSON into a `OpenWeatherResponse` instance" in {
      val json = shortJsonResponse.parseJson
      val r = json.convertTo[OpenWeatherResponse]
      // not checking every value, just a quick sampling
      r.lat shouldBe 2
      r.lon shouldBe 2
      r.current.temp shouldBe 84.79
    }
    "successfully turn a `OpenWeatherResponse` into a `CurrentWeather`" in {
      val json = shortJsonResponse.parseJson
      val r = json.convertTo[OpenWeatherResponse]
      val cw = CurrentWeather.fromAPIResponse(r)
      cw.activeAlerts shouldBe false
      cw.alerts shouldBe empty
      cw.temperatureDescription shouldBe TemperatureDescription.Moderate
      cw.weatherConditions should have size 1
      cw.weatherConditions.head shouldBe WeatherCondition.Cloudy("broken clouds")
    }
  }
}

