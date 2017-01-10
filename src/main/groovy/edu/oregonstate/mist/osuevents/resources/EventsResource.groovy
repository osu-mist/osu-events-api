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
import javax.validation.Valid
import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.QueryParam
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import static java.util.UUID.randomUUID

@Path('/events/')
@Produces(MediaType.APPLICATION_JSON)
class EventsResource extends Resource {
    //Logger logger = LoggerFactory.getLogger(EventsResource.class)

    private final EventsDAO eventsDAO

    EventsResource(EventsDAO eventsDAO) {
        this.eventsDAO = eventsDAO
    }

    private final String uuidRegEx =
            "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[34][0-9a-fA-F]{3}-[89ab][0-9a-fA-F]{3}-[0-9a-fA-F]{12}"

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
            //println(event.instances[1].start)
            resultObject.data = new ResourceObject(
                    id: event.eventID,
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
    public Response getEvents(@Auth AuthenticatedUser _, @QueryParam('format') String format) {

        def events = eventsDAO.getEvents()
        def resultObject = new ResultObject()
        resultObject.data = []

        events.each {
            it.instances = eventsDAO.getInstances(it.eventID)
        }

        if (!format) {
            events.each {
                resultObject.data += new ResourceObject(
                        id: it.eventID,
                        type: 'event',
                        attributes: it
                )
            }
        }
//        else if (format == "csv") {
//
//        } else if (format == "ics") {
//
//        } else {
//            return badRequest("Invalid format value. Valid formats are csv or ics.").build()
//        }
        ok(resultObject).build()
    }
/**
 * POST an event
 */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createEvent(@Auth AuthenticatedUser _,
                                @Valid ResourceObject newResourceObject) {

        Event newEvent = newResourceObject.attributes

        if (!newEvent.eventID) {
            newEvent.eventID = randomUUID() as String
        }

        if (!newEvent.eventID.matches(uuidRegEx)) {
            return Response.status(Response.Status.CONFLICT).entity("Conflict!").build()
        }

        eventsDAO.createEvent(
                newEvent.eventID, newEvent.title, newEvent.description, newEvent.location,
                newEvent.group, newEvent.department, newEvent.room, newEvent.address,
                newEvent.city, newEvent.state, newEvent.eventURL, newEvent.photoURL,
                newEvent.ticketURL, newEvent.facebookURL, newEvent.cost, newEvent.hashtag,
                newEvent.keywords, newEvent.tags, newEvent.allowsReviews, newEvent.sponsored,
                newEvent.venuePageOnly, newEvent.excludeFromTrending, newEvent.visibility)

        Event event = eventsDAO.getById(newEvent.eventID)
        def resultObject = new ResultObject()
        resultObject.data = new ResourceObject(
                id: event.eventID,
                type: 'event',
                attributes: event
        )
        created(resultObject).build()
    }
}
