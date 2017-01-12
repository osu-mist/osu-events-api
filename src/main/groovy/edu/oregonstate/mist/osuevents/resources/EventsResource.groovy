package edu.oregonstate.mist.osuevents.resources

import edu.oregonstate.mist.api.AuthenticatedUser
import edu.oregonstate.mist.api.Resource
import edu.oregonstate.mist.api.jsonapi.ResourceObject
import edu.oregonstate.mist.api.jsonapi.ResultObject
import edu.oregonstate.mist.osuevents.core.Event
import edu.oregonstate.mist.osuevents.db.EventsDAO
import edu.oregonstate.mist.osuevents.mapper.InstanceMapper
import groovy.json.JsonOutput
import io.dropwizard.auth.Auth
import org.codehaus.groovy.runtime.typehandling.GroovyCastException
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
import java.time.format.DateTimeParseException

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
        def resultObject = getResultObject(event)

        ok(resultObject).build()
    }

/**
 * GET all events
 */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEvents(@Auth AuthenticatedUser _, @QueryParam('format') String format) {

        def events = eventsDAO.getEvents()
        def resultObject = getResultObject(events)

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

        Event newEvent

        try {
            newEvent = newResourceObject.attributes
        } catch (GroovyCastException e) {
            return badRequest("Resource object contains unrecognized fields.").build()
        }

        newEvent.eventID = newEvent.eventID ?: randomUUID() as String

        if (!newEvent.eventID.matches(uuidRegEx) || eventsDAO.getById(newEvent.eventID)) {
            return conflict().build()
        }

        String customFieldData = JsonOutput.toJson(newEvent.customFields)
        String filterData = JsonOutput.toJson(newEvent.filters)

        eventsDAO.createEvent(
                newEvent.eventID, newEvent.title, newEvent.description, newEvent.location,
                newEvent.group, newEvent.department, newEvent.room, newEvent.address,
                newEvent.city, newEvent.state, newEvent.eventURL, newEvent.photoURL,
                newEvent.ticketURL, newEvent.facebookURL, newEvent.cost, newEvent.hashtag,
                newEvent.keywords, newEvent.tags, newEvent.allowsReviews, newEvent.sponsored,
                newEvent.venuePageOnly, newEvent.excludeFromTrending, newEvent.visibility,
                filterData, customFieldData)

        try {
            newEvent.instances.each {
                eventsDAO.createInstance(
                        it.id,
                        newEvent.eventID,
                        InstanceMapper.formatForDB(it.start.toString()),
                        InstanceMapper.formatForDB(it.end.toString())
                )
            }
        } catch (DateTimeParseException e) {
            return badRequest("Unable to parse date." +
                    "Dates should follow ISO 8601 specifications.").build()
        } catch (MissingMethodException e) {
            return badRequest("Unable to process instance." +
                    "Ensure instance ID is a string.").build()
        }

        Event event = eventsDAO.getById(newEvent.eventID)
        def resultObject = getResultObject(event)

        created(resultObject).build()
    }

    private ResultObject getResultObject(def events) {
        def resultObject = new ResultObject()
        resultObject.data = []

        events.each {
            it.instances = eventsDAO.getInstances(it.eventID)
            resultObject.data += new ResourceObject(
                    id: it.eventID,
                    type: 'event',
                    attributes: it
            )
        }
        resultObject
    }
}
