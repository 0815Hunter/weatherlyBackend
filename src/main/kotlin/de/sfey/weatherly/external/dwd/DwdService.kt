package de.sfey.weatherly.external.dwd

import de.sfey.weatherly.model.weather.ClimateDay
import de.sfey.weatherly.model.weather.WeatherStation
import org.eclipse.microprofile.rest.client.inject.RestClient
import java.io.ByteArrayInputStream
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import javax.inject.Inject
import javax.inject.Singleton
import javax.ws.rs.NotFoundException

@Singleton
class DwdService @Inject constructor(@RestClient val dwdApi: DwdApi) {

    fun getAllChristmasClimateDaysFor(station: WeatherStation): List<ClimateDay> {

        var fileName = ""
        dwdApi.getHtml().reader().forEachLine {
            // <a href="tageswerte_KL_00044_19690101_20211231_hist.zip">tageswerte_KL_00044_19690101_20211231_hist.zip</a>
            val indexOfFileName = it.indexOf("tageswerte_KL_${station.stationId}")

            if (indexOfFileName != -1) {
                val fileNameWithoutEnding = it.substring(indexOfFileName until it.indexOf(".zip\">"))
                fileName = "$fileNameWithoutEnding.zip"
            }
        }
        if (fileName.isEmpty()) {
            throw NotFoundException()
        }

        val zipInput = dwdApi.getFile(fileName)

        val zipInputStream = ZipInputStream(zipInput)
        var nextEntry: ZipEntry?
        do {
            nextEntry = zipInputStream.nextEntry
        } while (nextEntry != null && !nextEntry.name.startsWith("produkt_klima_tag_"))

        if (nextEntry == null) {
            throw NotFoundException()
        }

        val tageswerteBytes = zipInputStream.readNBytes(nextEntry.size.toInt())
        val tageswerteReader = ByteArrayInputStream(tageswerteBytes).reader()

        val christmasClimateDays = LinkedList<ClimateDay>().toMutableList()

        tageswerteReader.forEachLine {

            val climateDay = try {
                ClimateDay.fromDwdTageswerteLine(it)
            } catch (e: Exception) {
                return@forEachLine
            }

            val measureDate = climateDay.measureDate

            if (measureDate.monthValue == 12 && measureDate.dayOfMonth == 24) {
                climateDay.weatherStation = station
                christmasClimateDays.add(climateDay)
            }
        }

        zipInputStream.close()

        return christmasClimateDays

    }
}