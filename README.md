# jha-weather-app

Uses the [2.5 API for OpenWeatherMap](https://openweathermap.org/api/one-call-api) onecall as it doesn't require 
a subscription like 3.0 does

### Running the Weather App
To boot up the HTTP server, invoke SBT with:
```shell
./sbt run
```
Once the server boots up and begins listening you can
access the API to get the current weather. 

The HTTP Endpoint is `/weather`, and it takes two querystring arguments:
- `lon` - the longitude to check the weather at
- `lat` - the latitude to check the weather at

example with curl:

```shell
curl localhost:8080/weather\?lon=44.2706\&lat=71.3033
```

You should expect back a response containing:
- Temperature Description: This describes how it feels outside.
  - \< 55° is considered 'Cold'
  - \> 85° is considered 'Hot'
  - Anything else is considered 'Moderate'
- A List of Weather Conditions with description
- A boolean indicating whether there are active alerts
- A list, if there are any, of Weather Alerts

Example Output:
```json
{"activeAlerts":false,"alerts":[],"temperatureDescription":"Cold","weatherConditions":["broken clouds"]}
```

### Future Improvement Suggestions
- Better / more explicit error handling
- Add support for specifying Zip code or City + state; there are APIs on OpenWeatherMap that can resolve these into geo coordinates