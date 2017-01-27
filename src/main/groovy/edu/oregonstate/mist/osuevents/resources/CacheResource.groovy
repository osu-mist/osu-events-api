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
        def placesDB = placeToMap(eventsDAO.getPlaces())
        def places = cacheDAO.getPlaces()
        def changes = compareMapPair(placesDB, places)

        changes.deleted.each {
            eventsDAO.deletePlace(it.key)
        }

        changes.new.each {
            eventsDAO.createPlace(new Place(
                        id: it.key,
                        name: it.value
                ))
        }

        changes.updated.each {
            eventsDAO.updatePlace(new Place(
                        id: it.key,
                        name: it.value
                ))

        }

        ok(changes).build()
    }

    private def placeToMap(List<Place> places) {
        def placesMap = [:]

        places.each {
            placesMap[new String("${it.id}")] = new String("${it.name}")
        }
        placesMap
    }

    private def compareMapPair(def oldMap, def newMap) {
        def newKeys = newMap*.key
        def oldKeys = oldMap*.key

        def removedKeys = oldKeys - newKeys
        def addedKeys = newKeys - oldKeys
        def commonKeys = newKeys - removedKeys - addedKeys
        def changedKeys = commonKeys.findAll { oldMap[it] != newMap[it] }
        def unchangedKeys = commonKeys - changedKeys

        [
                deleted: oldMap.findAll { it.key in removedKeys },
                new: newMap.findAll { it.key in addedKeys },
                updated: newMap.findAll { it.key in changedKeys },
                unchanged: newMap.findAll { it.key in unchangedKeys }
        ]
    }
}
