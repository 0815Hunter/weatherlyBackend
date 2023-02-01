package de.sfey.weatherly.external.back4app

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal

data class Back4AppResponse(@JsonProperty("results") val countries: List<Back4AppCountry>)

data class Back4AppCountry(
    @JsonProperty("cityId") val cityId: Long,
    @JsonProperty("name") val name: String,
    @JsonProperty("location") val location: Back4AppGeoLocation
)

data class Back4AppGeoLocation(
    @JsonProperty("latitude") val latitude: BigDecimal,
    @JsonProperty("longitude") val longitude: BigDecimal
)

data class Back4AppCountBody(@JsonProperty("count") val count: Int)