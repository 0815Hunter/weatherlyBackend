package de.sfey.weatherly.external.dwd

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient
import java.io.InputStream
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces


@Path("/climate_environment/CDC/observations_germany/climate/daily/kl/historical")
@RegisterRestClient(configKey = "dwdApi")
interface DwdApi {

    @GET
    @Path("/{fileName}")
    @Produces("application/zip")
    fun getFile(@PathParam("fileName") fileName: String): InputStream


    @GET
    @Path("/")
    @Produces("text/html")
    fun getHtml(): InputStream

}