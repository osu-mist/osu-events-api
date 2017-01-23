package edu.oregonstate.mist.osuevents.resources

import edu.oregonstate.mist.api.AuthenticatedUser
import edu.oregonstate.mist.api.Error
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

    Logger logger = LoggerFactory.getLogger(EventsResource.class)

    private final EventsDAO eventsDAO

    EventsResource(EventsDAO eventsDAO) {
        this.eventsDAO = eventsDAO
    }

    //used to check if client-generated id is a valid UUID
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
        List<Error> errors = getErrors(newResultObject)

        if (errors) {
            Response.ResponseBuilder responseBuilder = Response.status(Response.Status.BAD_REQUEST)
            return responseBuilder.entity(errors).build()
        }
        try {
            ResourceObject newResourceObject = newResultObject.data
            Event newEvent = newResultObject.data.attributes

            //generate id if one wasn't included in the request
            newResourceObject.id = newResourceObject.id ?: randomUUID() as String

            //get custom fields and filters ready for DAO
            String customFieldData = JsonOutput.toJson(newEvent.customFields)
            String filterData = JsonOutput.toJson(newEvent.filters)

            newEvent.instances.each {
                eventsDAO.createInstance(
                        it.id.toString(),
                        newResourceObject.id,
                        InstanceMapper.formatForDB(it.start.toString()),
                        InstanceMapper.formatForDB(it.end.toString())
                )
            }

            eventsDAO.createEvent(newResourceObject.id, newEvent, filterData, customFieldData)

            //get newly created event and put it in response
            ResourceObject event = eventsDAO.getById(newResourceObject.id)
            created(getResultObject(event)).build()
        } catch (Exception e) {
            logger.error("Exception while calling createEvent", e)
            return internalServerError(ErrorMessages.unexpectedException).build()
        }
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
        List<Error> errors = getErrors(resultObject, true, id)

        if (errors) {
            Response.ResponseBuilder responseBuilder = Response.status(Response.Status.BAD_REQUEST)
            return responseBuilder.entity(errors).build()
        }

        ResourceObject currentResourceObject = eventsDAO.getById(id)
        Event updatedEvent

        updatedEvent = currentResourceObject.attributes
        resultObject.data.attributes.each { key, value ->
            updatedEvent."$key" = value
        }

        //get custom fields and filters ready for DAO
        String customFieldData = JsonOutput.toJson(updatedEvent.customFields)
        String filterData = JsonOutput.toJson(updatedEvent.filters)

        updatedEvent.instances.each {
            String start = it.start.toString()
            String end = it.end.toString()

            //if instance doesn't exist, create it
            if (!eventsDAO.getInstance(id, it.id.toString())) {
                eventsDAO.createInstance(
                        it.id.toString(),
                        id,
                        InstanceMapper.formatForDB(start),
                        InstanceMapper.formatForDB(end)
                )
            //if start and end values are null, delete the instance
            } else if ((start == "null") && (end == "null")) {
                eventsDAO.deleteInstance(id, it.id.toString())
            } else {
                eventsDAO.updateInstance(
                        it.id.toString(),
                        id,
                        InstanceMapper.formatForDB(start),
                        InstanceMapper.formatForDB(end)
                )
            }
        }

        eventsDAO.updateEvent(id, updatedEvent, filterData, customFieldData)

        //get updated event and put it in response
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
 * Helper function for data validation. Returns list of applicable errors.
 */
    private List<Error> getErrors(ResultObject resultObject,
                                  Boolean update = false,
                                  String pathID = null) {

        List<Error> errors = []
        ResourceObject resourceObject
        Event event

        if (update) {
            if (!eventsDAO.getById(resultObject.data.id)) {
                errors.add(new Error(
                        status: 404,
                        developerMessage: Resource.properties.get('notFound.developerMessage'),
                        userMessage: Resource.properties.get('notFound.userMessage'),
                        code: Integer.parseInt(Resource.properties.get('notFound.code')),
                        details: Resource.properties.get('notFound.details')
                ))
            }
            if (resultObject.data.id != pathID) {
                errors.add(new Error(
                        status: 400,
                        developerMessage: "ID in JSON body must match ID in path parameter",
                        userMessage: Resource.properties.get('badRequest.userMessage'),
                        code: Integer.parseInt(Resource.properties.get('badRequest.code')),
                        details: Resource.properties.get('badRequest.details')
                ))
            }
        }

        if (resultObject.data.id && !update) {
            resultObject.data.id = resultObject.data.id.toString()

            if (!resultObject.data.id.matches(uuidRegEx)) {
                errors.add(new Error(
                        status: 409,
                        developerMessage: ErrorMessages.invalidUUID
                ))
            }
            if (eventsDAO.getById(resultObject.data.id)) {
                errors.add(new Error(
                        status: 409,
                        developerMessage: ErrorMessages.idExists
                ))
            }
        }

        try {
            resourceObject = resultObject.data
            event = resultObject.data.attributes
        } catch (GroovyCastException e) {
            errors.add(new Error(
                    status: 400,
                    developerMessage: ErrorMessages.unknownFields,
                    userMessage: Resource.properties.get('badRequest.userMessage'),
                    code: Integer.parseInt(Resource.properties.get('badRequest.code')),
                    details: Resource.properties.get('badRequest.details')
            ))
            return errors
        }

        event.instances.each {
            try {
                InstanceMapper.formatForDB(it.start.toString())
                InstanceMapper.formatForDB(it.end.toString())
            } catch (DateTimeParseException e) {
                errors.add(new Error(
                        status: 400,
                        developerMessage: "Error with instance ID: ${it.id}. " +
                                ErrorMessages.parseDate,
                        userMessage: Resource.properties.get('badRequest.userMessage'),
                        code: Integer.parseInt(Resource.properties.get('badRequest.code')),
                        details: Resource.properties.get('badRequest.details')
                ))
            }
        }
        errors
    }
/**
 * Helper function for preparing a Result Object
 */
    private ResultObject getResultObject(def events) {
        def resultObject = new ResultObject()
        resultObject.data = []

        events.each {
            it.attributes.instances = eventsDAO.getInstances(it.id)
            resultObject.data += it
        }
        resultObject
    }
}
