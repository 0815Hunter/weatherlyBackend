package de.sfey.weatherly.external.back4app

import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.QueryParam

@Path("/classes")
@RegisterRestClient(configKey = "back4AppApi")
@RegisterClientHeaders(Back4AppApiHeaders::class)
interface Back4AppApi {

    @GET
    @Path("/Germanycities_City")
    fun getGermanCitiesCount(
        @QueryParam("limit") limit: Int = 0,
        @QueryParam("count") count: Int = 1
    ): Back4AppCountBody

    @GET
    @Path("/Germanycities_City")
    fun getCities(
        @QueryParam("keys") keys: String = "cityId,name,location",
        @QueryParam("skip") skip: Int = 0,
        @QueryParam("limit") limit: Int = 100
    ): Back4AppResponse
}