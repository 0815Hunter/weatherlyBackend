package de.sfey.weatherly.api.cities

import de.sfey.weatherly.external.back4app.Back4AppApi
import de.sfey.weatherly.model.city.City
import de.sfey.weatherly.model.city.GeoLocation
import io.quarkus.panache.common.Page
import org.eclipse.microprofile.rest.client.inject.RestClient
import org.jboss.resteasy.reactive.RestQuery
import javax.inject.Inject
import javax.transaction.Transactional
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path

@Path("/api/cities")
class CityResource @Inject constructor(@RestClient val back4AppApi: Back4AppApi) {

    @GET
    fun find(@RestQuery name: String?, @RestQuery("name-like") nameLike: String?): List<City?> {

        if (name != null) {
            return listOf(City.findByName(name))
        }
        if (nameLike != null) {
            val likeNameQueryParam = "%$nameLike%"
            return City.findByNameLike(likeNameQueryParam)
        }

        return City.findAll().page(Page.ofSize(50)).list()

    }

    @POST
    @Path("/init")
    @Transactional
    fun initGermanCities() {
        val back4AppCountBody = back4AppApi.getGermanCitiesCount()

        val count = back4AppCountBody.count

        val limit = 100
        val loops = count / limit

        for (i in 0..loops) {

            val back4AppCountryResponse = back4AppApi.getCities(skip = i * limit, limit = limit)

            //TODO for updating: select countries by cityId from db, update if present, else insert
            back4AppCountryResponse.countries.forEach { back4AppCountry ->
                val city = City(
                    cityId = back4AppCountry.cityId,
                    name = back4AppCountry.name,
                    geoLocation = GeoLocation(
                        lat = back4AppCountry.location.latitude,
                        long = back4AppCountry.location.longitude
                    )
                )

                city.persist()
            }

            City.flush()
        }
    }
}