package edu.oregonstate.mist.osuevents.resources

import edu.oregonstate.mist.api.AuthenticatedUser
import edu.oregonstate.mist.api.Resource
import edu.oregonstate.mist.osuevents.core.Place
import edu.oregonstate.mist.osuevents.db.CacheDAO
import edu.oregonstate.mist.osuevents.db.EventsDAO
import io.dropwizard.auth.Auth
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.ws.rs.PUT
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path('/cache/update/')
@Produces(MediaType.APPLICATION_JSON)
class CacheResource extends Resource {
    Logger logger = LoggerFactory.getLogger(CacheResource.class)

    private final CacheDAO cacheDAO
    private final EventsDAO eventsDAO

    CacheResource(CacheDAO cacheDAO, EventsDAO eventsDAO) {
        this.cacheDAO = cacheDAO
        this.eventsDAO = eventsDAO
    }

    @PUT
    @Path('places')
    @Produces(MediaType.APPLICATION_JSON)
    public Response updatePlaces(@Auth AuthenticatedUser _) {
        List<Place> places = cacheDAO.getPlaces()

        places.each {
            println("ID: ${it.id}, Name: ${it.name}")
            Place place = eventsDAO.getPlace(it.id)

            if (!place) {
                println("Place doesn't exist.")
                eventsDAO.createPlace(it)
            } else if (place.name != it.name) {
                println("Update the Name")
                eventsDAO.updatePlace(place.id, it.name)
            } else {
                println ("Place exists.")
            }
        }

        ok(places).build()
    }
}
