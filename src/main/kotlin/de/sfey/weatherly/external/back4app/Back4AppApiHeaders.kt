package de.sfey.weatherly.external.back4app

import org.eclipse.microprofile.rest.client.ext.ClientHeadersFactory
import javax.enterprise.context.ApplicationScoped
import javax.ws.rs.core.MultivaluedHashMap
import javax.ws.rs.core.MultivaluedMap

@ApplicationScoped
class Back4AppApiHeaders : ClientHeadersFactory {
    override fun update(incomingHeaders: MultivaluedMap<String, String>?, clientOutgoingHeaders: MultivaluedMap<String, String>?): MultivaluedMap<String, String> {
        val multivaluedHashMap = MultivaluedHashMap<String, String>()

        multivaluedHashMap.add("X-Parse-Application-Id", "M25Q3l9BT9Jas0CyDlpbSMCxV6TN6qHpPyvWDASb")
        multivaluedHashMap.add("X-Parse-REST-API-Key", "zgXJxKY6AcEsEnPyGBxnXAjundUOamOQJuVx4NiY")
        return multivaluedHashMap
    }
}