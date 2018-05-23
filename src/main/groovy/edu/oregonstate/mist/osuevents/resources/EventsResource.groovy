package edu.oregonstate.mist.osuevents.resources

import com.codahale.metrics.annotation.Timed
import edu.oregonstate.mist.api.Error
import edu.oregonstate.mist.api.Resource
import edu.oregonstate.mist.api.jsonapi.ResourceObject
import edu.oregonstate.mist.api.jsonapi.ResultObject
import edu.oregonstate.mist.osuevents.core.Event
import edu.oregonstate.mist.osuevents.core.EventException
import edu.oregonstate.mist.osuevents.db.EventsDAOWrapper
import groovy.transform.TypeChecked
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.annotation.security.PermitAll
import javax.validation.Valid
import javax.ws.rs.Consumes
import javax.ws.rs.DELETE
import javax.ws.rs.GET
import javax.ws.rs.HeaderParam
import javax.ws.rs.POST
import javax.ws.rs.PUT
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import java.time.ZoneId
import java.time.format.DateTimeParseException

import static java.util.UUID.randomUUID
import com.opencsv.CSVWriter

@Path('/calendar/events')
@Produces(MediaType.APPLICATION_JSON)
@PermitAll
@TypeChecked
class EventsResource extends Resource {

    Logger logger = LoggerFactory.getLogger(EventsResource.class)

    private final EventsDAOWrapper eventsDAOWrapper
    private final ZoneId backendTimezone

    EventsResource(EventsDAOWrapper eventsDAOWrapper, String backendTimezone = "UTC") {
        this.eventsDAOWrapper = eventsDAOWrapper
        this.backendTimezone = ZoneId.of(backendTimezone)
    }

    @GET
    @Timed
    @Path('{id: [0-9a-zA-Z-]+}')
    Response getEventByID(@PathParam('id') String eventID) {
        Event event = eventsDAOWrapper.getEventByID(eventID)

        if (event) {
            ok(eventResultObject(event)).build()
        } else {
            notFound().build()
        }
    }

    @GET
    @Timed
    Response getEvents() {
        ok(eventResultObject(eventsDAOWrapper.getEvents())).build()
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Timed
    Response createEvent(@Valid ResultObject resultObject,
                         @HeaderParam("OSU-API-ActAs") String actAs) {
        List<Error> validationErrors = eventErrors(resultObject)

        if (validationErrors) {
            return errorArrayResponse(validationErrors)
        }

        Event event = Event.fromResultObject(resultObject)
        event.owner = actAs

        Event createdEvent = eventsDAOWrapper.createEvent(event)

        created(eventResultObject(createdEvent)).build()
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Timed
    @Path('{id: [0-9a-zA-Z-]+}')
    Response updateEvent(@PathParam('id') String eventID,
                         @HeaderParam("OSU-API-ActAs") String actAs,
                         @Valid ResultObject resultObject) {
        // Check that the event exists and the user can update it
        Response error = updateChecker(eventID, actAs)

        if (error) {
            return error
        }

        // Check the data in the request body is valid
        List<Error> validationErrors = eventErrors(resultObject)

        if (validationErrors) {
            return errorArrayResponse(validationErrors)
        }

        Event event = Event.fromResultObject(resultObject)
        event.eventID = eventID

        Event updatedEvent = eventsDAOWrapper.updateEvent(event)

        ok(eventResultObject(updatedEvent)).build()
    }

    @DELETE
    @Timed
    @Path('{id: [0-9a-zA-Z-]+}')
    Response updateEvent(@PathParam('id') String eventID,
                         @HeaderParam("OSU-API-ActAs") String actAs) {
        // Check that the event exists and the user can update it
        Response error = updateChecker(eventID, actAs)

        if (error) {
            return error
        }

        eventsDAOWrapper.deleteEvent(eventID)

        noContent().build()
    }

    private Response updateChecker(String eventID, String actAs) {
        Event event = eventsDAOWrapper.getEventByID(eventID)

        if (!event) {
            notFound().build()
        } else if (event.owner != actAs) {
            forbidden().build()
        } else {
            null
        }
    }

    private Response errorArrayResponse(List<Error> errors) {
        Response.ResponseBuilder responseBuilder = Response.status(Response.Status.BAD_REQUEST)
        responseBuilder.entity(errors).build()
    }

    private List<Error> eventErrors(ResultObject resultObject) {
        List<Error> errors = []

        Event event

        def addBadRequest = { String message ->
            errors.add(Error.badRequest(message))
        }

        try {
            event = Event.fromResultObject(resultObject)
        } catch (EventException e) {
            addBadRequest("Could not parse job object. " +
                    "Make sure dates are in ISO8601 format: yyyy-MM-dd")

            // if we can't deserialize the job object, no need to proceed
            return errors
        }

        if (!event) {
            addBadRequest("No event object provided.")

            // if there's no job object, no need to proceed
            return errors
        }

        if (event.hashtag && event.hashtag.contains("#")) {
            addBadRequest("Hashtag cannot contain '#'.")
        }

        //required fields gathered from https://events.oregonstate.edu/event/create
        //start date is also required, but that will be done in the instances validation
        def requiredFields = [
                "Event title": event.title,
                "Description": event.description,
                "Contact name": event.contactName,
                "Contact email": event.contactEmail
        ]

        requiredFields.findAll { key, value -> !value }.each { key, value ->
            addBadRequest("${key} is required.")
        }

        //TODO: validate ID fields

        if (!event.instances) {
            addBadRequest("At least one event instance is required.")
        } else {
            if (event.instances.find { !it.start }) {
                addBadRequest("An instance can not have a null start time.")
            }
            if (event.instances.find { it.start && it.end && (it.start >= it.end) }) {
                addBadRequest("The start time of an instance must occur before the end time.")
            }
        }
        errors
    }

    private ResultObject eventResultObject(List<Event> events) {
        new ResultObject(data: events.collect { eventResourceObject(it) })
    }

    private ResultObject eventResultObject(Event event) {
        new ResultObject(data: eventResourceObject(event))
    }

    private ResourceObject eventResourceObject(Event event) {
        new ResourceObject(
                id: event.eventID,
                type: 'events',
                attributes: event
        )
    }

}
