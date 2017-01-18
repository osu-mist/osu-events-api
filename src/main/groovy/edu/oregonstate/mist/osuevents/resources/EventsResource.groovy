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
import edu.oregonstate.mist.api.PATCH
import org.codehaus.groovy.runtime.typehandling.GroovyCastException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.validation.Valid
import javax.ws.rs.Consumes
import javax.ws.rs.DELETE
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
    @Path('{id: [0-9a-zA-Z-]+}')
    @Produces(MediaType.APPLICATION_JSON)
    public Response getByID(@Auth AuthenticatedUser _,
                            @PathParam('id') String id) {
        ResourceObject event = eventsDAO.getById(id)
        ok(getResultObject(event)).build()
    }

/**
 * GET all events
 */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEvents(@Auth AuthenticatedUser _,
                              @QueryParam('format') String format) {
        def events = eventsDAO.getEvents()
//@TODO add methods for returning csv and ics formats
//        else if (format == "csv") {
//
//        } else if (format == "ics") {
//
//        } else {
//            return badRequest("Invalid format value. Valid formats are csv or ics.").build()
//        }
        ok(getResultObject(events)).build()
    }
/**
 * POST an event
 */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createEvent(@Auth AuthenticatedUser _,
                                @Valid ResultObject newResultObject) {
        ResourceObject newResourceObject
        Event newEvent

        try {
            newResourceObject = newResultObject.data
            newEvent = newResultObject.data.attributes
        } catch (GroovyCastException e) {
            return badRequest(ErrorMessages.unknownFields).build()
        }

        newResourceObject.id = newResourceObject.id ?: randomUUID() as String

        if (!newResourceObject.id.matches(uuidRegEx) || eventsDAO.getById(newResourceObject.id)) {
            return conflict().build()
        }

        String customFieldData = JsonOutput.toJson(newEvent.customFields)
        String filterData = JsonOutput.toJson(newEvent.filters)

        eventsDAO.createEvent(newResourceObject.id, newEvent, filterData, customFieldData)

        try {
            newEvent.instances.each {
                eventsDAO.createInstance(
                        it.id,
                        newResourceObject.id,
                        InstanceMapper.formatForDB(it.start.toString()),
                        InstanceMapper.formatForDB(it.end.toString())
                )
            }
        } catch (DateTimeParseException e) {
            return badRequest(ErrorMessages.parseDate).build()
        } catch (MissingMethodException e) {
            return badRequest(ErrorMessages.processInstance).build()
        }

        //Get newly created event and put it in response
        ResourceObject event = eventsDAO.getById(newResourceObject.id)
        created(getResultObject(event)).build()
    }

/**
 * PATCH an event
 */
    @PATCH
    @Path('{id: [0-9a-zA-Z-]+}')
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateEvent(@Auth AuthenticatedUser _,
                                @PathParam('id') String id,
                                @Valid ResultObject resultObject) {
        ResourceObject currentResourceObject = eventsDAO.getById(id)

        if (!currentResourceObject) {
            return notFound().build()
        } else if (resultObject.data.id != id) {
            return badRequest("ID in JSON body must match ID in path parameter").build()
        }

        Event updatedEvent

        try {
            updatedEvent = currentResourceObject.attributes
            resultObject.data.attributes.each { key, value ->
                updatedEvent."$key" = value
            }
        } catch (MissingPropertyException e) {
            return badRequest(ErrorMessages.unknownFields).build()
        }

        String customFieldData = JsonOutput.toJson(updatedEvent.customFields)
        String filterData = JsonOutput.toJson(updatedEvent.filters)

        eventsDAO.updateEvent(id, updatedEvent, filterData, customFieldData)

        try {
            updatedEvent.instances.each {
                if (!eventsDAO.getInstance(id, it.id)) {
                    eventsDAO.createInstance(
                            it.id,
                            id,
                            InstanceMapper.formatForDB(it.start.toString()),
                            InstanceMapper.formatForDB(it.end.toString())
                    )
                } else {
                    eventsDAO.updateInstance(
                            it.id,
                            id,
                            InstanceMapper.formatForDB(it.start.toString()),
                            InstanceMapper.formatForDB(it.end.toString())
                    )
                }
            }
        } catch (DateTimeParseException e) {
            return badRequest(ErrorMessages.parseDate).build()
        } catch (MissingMethodException e) {
            return badRequest(ErrorMessages.processInstance).build()
        }

        ok(getResultObject(eventsDAO.getById(id))).build()
    }
/**
 * DELETE an event
 */
    @DELETE
    @Path('{id: [0-9a-zA-Z-]+}')
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteEvent(@Auth AuthenticatedUser _,
                                @PathParam('id') String id) {
        if (!eventsDAO.getById(id)) {
            return notFound().build()
        }

        eventsDAO.deleteEvent(id)
        Response.noContent().build()
    }

/**
 * Helper function for preparing a Result Object
 */
    private ResultObject getResultObject(def events) {
        def resultObject = new ResultObject()
        resultObject.data = []

        events.each {
            println(it.id)
            it.attributes.instances = eventsDAO.getInstances(it.id)
            resultObject.data += it
        }
        resultObject
    }
}
