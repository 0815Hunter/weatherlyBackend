package de.sfey.weatherly.api.weather

import de.sfey.weatherly.external.dwd.DwdService
import de.sfey.weatherly.model.city.GeoLocation
import de.sfey.weatherly.model.weather.ClimateDay
import de.sfey.weatherly.model.weather.WeatherStation
import io.quarkus.cache.CacheResult
import org.jboss.resteasy.reactive.RestPath
import org.jboss.resteasy.reactive.RestQuery
import java.io.FileReader
import java.math.BigDecimal
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.transaction.Transactional
import javax.ws.rs.GET
import javax.ws.rs.NotFoundException
import javax.ws.rs.POST
import javax.ws.rs.Path

@Path("/api/weather")
class WeatherResource @Inject constructor(val dwdService: DwdService) {

    @GET
    @CacheResult(cacheName = "nearest-weather-station-cache") // TODO configure cache settings
    @Transactional
    fun getNearestByGeolocation(@RestQuery lat: BigDecimal, @RestQuery long: BigDecimal): WeatherStation {

        val geoLocation = GeoLocation(lat = lat, long = long)

        val closestStation = WeatherStation.streamAll().min { o1, o2 ->
            val d1 = geoLocation.distanceTo(o1.geoLocation)
            val d2 = geoLocation.distanceTo(o2.geoLocation)

            d1.compareTo(d2)
        }.orElseThrow()

        return closestStation

    }

    @GET
    @Path("/{stationId}/christmas")
    @CacheResult(cacheName = "station-christmas-cache")
    @Transactional
    fun getHistoricalWeatherAtStationAtChristmas(@RestPath stationId: String): List<ClimateDayDto> {
        val station = WeatherStation.findByStationId(stationId) ?: throw NotFoundException()

        val fromDate = station.fromDate
        val toDate = station.toDate

        // TODO flexible search in the future: have a calendar table(id, year month day), associate id with climateday id
        val christmasDatesOfStation = (fromDate.year..toDate.year).map { LocalDate.of(it, 12, 24) }

        var climateDays = ClimateDay.findByStationIdAndDates(stationId, christmasDatesOfStation)

        if (climateDays.isEmpty()) {
            climateDays = dwdService.getAllChristmasClimateDaysFor(station)
            ClimateDay.persist(climateDays)
        }

        return climateDays.map { ClimateDayDto(it.measureDate, it.meanTemperature) }
    }

    @POST
    @Path("/initStations")
    @Transactional
    fun mapWeatherStationCsvToDb(): List<WeatherStation> {

        val weatherStationsUrl = ClassLoader.getSystemResource("weather-stations.txt")

        val fileReader = FileReader(weatherStationsUrl.file)

        val lines = fileReader.readLines().drop(2)

        val weatherStations = lines.map { it.trim() }
            .map {
                val stationsNameIndex = it.indexOfFirst { c -> c.isLetter() }
                val numberValues = it
                    .slice(0 until stationsNameIndex - 1)
                    .split("\\s+".toRegex())

                val stationNameAndState = it.substring(stationsNameIndex)
                    .split("(\\s\\s)+".toRegex())
                val stationName = "${stationNameAndState[0]}, ${stationNameAndState[1]}"

                WeatherStation(
                    stationId = numberValues[0],
                    stationName = stationName,
                    fromDate = LocalDate.parse(numberValues[1], DateTimeFormatter.BASIC_ISO_DATE),
                    toDate = LocalDate.parse(numberValues[2], DateTimeFormatter.BASIC_ISO_DATE),
                    geoLocation = GeoLocation(
                        lat = numberValues[4].toBigDecimal(),
                        long = numberValues[5].toBigDecimal()
                    )
                )
            }

        //TODO for updating: select weatherStations by id from db, update if present, else insert
        WeatherStation.persist(weatherStations)

        return weatherStations

    }
}