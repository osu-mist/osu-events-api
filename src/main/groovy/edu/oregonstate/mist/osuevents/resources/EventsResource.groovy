package edu.oregonstate.mist.osuevents.resources

import edu.oregonstate.mist.api.AuthenticatedUser
import edu.oregonstate.mist.api.Resource
import edu.oregonstate.mist.api.jsonapi.ResourceObject
import edu.oregonstate.mist.api.jsonapi.ResultObject
import edu.oregonstate.mist.osuevents.core.Event
import edu.oregonstate.mist.osuevents.db.EventsDAO
import io.dropwizard.auth.Auth
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
    @Path('{id: [0-9a-zA-Z]+}')
    @Produces(MediaType.APPLICATION_JSON)
    public Response getByID(@Auth AuthenticatedUser _, @PathParam('id') String id) {

        Event event = eventsDAO.getById(id)
        def resultObject = new ResultObject()

        if (event) {
            event.instances = eventsDAO.getInstances(id)
            resultObject.data = new ResourceObject(
                    id: event.event_id,
                    type: 'event',
                    attributes: event
            )
        }
        ok(resultObject).build()
    }

/**
 * GET all events
 */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEvents(@Auth AuthenticatedUser _) {

        def events = eventsDAO.getEvents()
        def resultObject = new ResultObject()
        resultObject.data = []

        events.each {
            it.instances = eventsDAO.getInstances(it.event_id)
            resultObject.data += new ResourceObject(
                    id: it.event_id,
                    type: 'event',
                    attributes: it
            )
        }
        ok(resultObject).build()
    }
}