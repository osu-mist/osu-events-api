package edu.oregonstate.mist.osuevents.resources

import edu.oregonstate.mist.api.AuthenticatedUser
import edu.oregonstate.mist.api.Resource
import edu.oregonstate.mist.osuevents.db.CacheDAO
import io.dropwizard.auth.Auth
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.ws.rs.GET
import javax.ws.rs.PUT
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path('/cache/')
@Produces(MediaType.APPLICATION_JSON)
class CacheResource extends Resource {
    Logger logger = LoggerFactory.getLogger(CacheResource.class)

    private final CacheDAO cacheDAO

    CacheResource(CacheDAO cacheDAO) {
        this.cacheDAO = cacheDAO
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response updatePlaces(@Auth AuthenticatedUser _) {
        ok(cacheDAO.getPlaces()).build()
    }
}
