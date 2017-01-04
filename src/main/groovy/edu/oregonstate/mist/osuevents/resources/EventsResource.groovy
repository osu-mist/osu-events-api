package edu.oregonstate.mist.osuevents.resources

import edu.oregonstate.mist.api.Resource
import edu.oregonstate.mist.osuevents.core.Event
import edu.oregonstate.mist.osuevents.db.EventsDAO
import io.dropwizard.jersey.params.IntParam
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path('/events/')
@Produces(MediaType.APPLICATION_JSON)
class EventsResource extends Resource {
    //Logger logger = LoggerFactory.getLogger(EventsResource.class)

    private final EventsDAO eventsDAO

    EventsResource(EventsDAO eventsDAO) {
        this.eventsDAO = eventsDAO
    }

/**
 * GET by ID
 */
    @GET
    @Path('{id: \\d+}')
    @Produces(MediaType.APPLICATION_JSON)
    public Response getByID(@PathParam('id') IntParam id) {

        Response returnResponse
        Event event = eventsDAO.getById(id.get())

        if (event == null) {
            returnResponse = notFound().build()
        } else {
            returnResponse = ok(event).build()
        }
        returnResponse
    }
}