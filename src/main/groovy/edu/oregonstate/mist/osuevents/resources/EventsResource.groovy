package edu.oregonstate.mist.osuevents.resources

import edu.oregonstate.mist.api.AuthenticatedUser
import edu.oregonstate.mist.api.Error
import edu.oregonstate.mist.api.Resource
import edu.oregonstate.mist.api.jsonapi.ResourceObject
import edu.oregonstate.mist.api.jsonapi.ResultObject
import edu.oregonstate.mist.osuevents.core.CacheObject
import edu.oregonstate.mist.osuevents.core.Event
import edu.oregonstate.mist.osuevents.core.Instance
import edu.oregonstate.mist.osuevents.db.EventsDAO
import edu.oregonstate.mist.osuevents.mapper.InstanceMapper
import edu.oregonstate.mist.osuevents.resources.CSVHelperFunctions
import groovy.json.JsonSlurper
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
import java.time.ZoneId
import java.time.format.DateTimeParseException

import static java.util.UUID.randomUUID
import com.opencsv.CSVWriter

@Path('/events/')
@Produces(MediaType.APPLICATION_JSON)
class EventsResource extends Resource {

    Logger logger = LoggerFactory.getLogger(EventsResource.class)

    private final EventsDAO eventsDAO
    private final ZoneId backendTimezone

    EventsResource(EventsDAO eventsDAO, String backendTimezone = "UTC") {
        this.eventsDAO = eventsDAO
        this.backendTimezone = ZoneId.of(backendTimezone)
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

        if (!event) {
            return notFound().build()
        }

        ok(getResultObject(event)).build()
    }

/**
 * GET all events
 */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    //TODO Add CSV MediaType to response object
    public Response getEvents(@Auth AuthenticatedUser _,
                              @QueryParam('format') String format) {
        List<ResourceObject> events = eventsDAO.getEvents()
        def resultObject

        if (!format) {
            events.each {
                resultObject.data += new ResourceObject(
                        id: it.eventID,
                        type: 'event',
                        attributes: it
                )
            }
        } else if (format == "csv") {
            final ArrayList<String> BASECSVHEADER =
                    ["EventID","Title" , "Description" , "Date From" , "Date To" ,
                     "Start Time", "End Time" , "Location" , "Address" , "City" , "State" ,
                     "Event Website" , "Room" , "Keywords" , "Tags" , "Photo URL" ,
                     "Ticket URL" , "Cost" , "Hashtag" , "Facebook URL" , "Group" ,
                     "Department" , "Allow User Activity" , "Allow User Attendance" ,
                     "Visibility" , "Featured Tabs" , "Sponsored" , "Venue Page Only" ,
                     "Exclude From Trending"]

            //TODO Handle localist default "Event Types" filter as another collumn.
            //Note Event Types is a filter
            ArrayList<String> csvheader = BASECSVHEADER.clone()

            def fields = eventsDAO.getCustomFields()
            fields.each {
                csvheader += it.name
            }

            def time = new Date().time
            String csvfilename = "events_gospel" + time + ".csv"
            CSVWriter writer = new CSVWriter(new FileWriter(csvfilename))
            writer.writeNext((String [] ) csvheader.toArray())

            //TODO FILTERS

            resultObject = getResultObject(events)
            resultObject.data.each {
                String eventid = it.id
                Event baseEvent = it.attributes as Event

                ArrayList<String> baseEventRecord =
                        [eventid, baseEvent.title, baseEvent.description,
                        "DATE_FROM_PLACE_HOLDER", "DATE_TO_PLACE_HOLDER", "START_TIME_PLACE_HOLDER",
                         "END_TIME_PLACE_HOLDER",
                        baseEvent.location, baseEvent.address, baseEvent.city, baseEvent.state,
                         baseEvent.eventURL, baseEvent.room, baseEvent.keywords, baseEvent.tags,
                         baseEvent.photoURL, baseEvent.ticketURL, baseEvent.cost, baseEvent.hashtag,
                         baseEvent.facebookURL, baseEvent.group, baseEvent.department,
                         "Allow_User_Activity", "USER_ATTENDANCE_FIELD", baseEvent.visibility,
                         "FEATURED_TABS", baseEvent.sponsored, baseEvent.venuePageOnly,
                         baseEvent.excludeFromTrending ]

                def eventCFieldsMap = baseEvent.customFields.collectEntries {[it.field,it.value]}
                fields.each {
                    String entryVal = eventCFieldsMap[it.name]
                    if(entryVal == null) {
                        entryVal = ""
                    }
                    baseEventRecord.add(entryVal)
                }

                List<Instance> eventInstances = eventsDAO.getInstances(eventid)

                eventInstances.each {
                    String[] instanceRecord = baseEventRecord.clone()

                    //"DATE_FROM_PLACE_HOLDER"
                    instanceRecord[3] = CSVHelperFunctions.getCSVDate(it.start,backendTimezone)
                    //"DATE_TO_PLACE_HOLDER"
                    instanceRecord[4] = CSVHelperFunctions.getCSVDate(it.end,backendTimezone)
                    //"START_TIME_PLACE_HOLDER"
                    instanceRecord[5] = CSVHelperFunctions.getCSVTime(it.start, backendTimezone)
                    //"END_TIME_PLACE_HOLDER"
                    instanceRecord[6] = CSVHelperFunctions.getCSVTime(it.end, backendTimezone)

                    writer.writeNext(instanceRecord)
                    //TODO Handle "Allow_User_Activity","USER_ATTENDANCE_FIELD", "FEATURED_TABS"
                }
            }
            writer.close()
            //TODO Add clean up for this file
        } // else if (format == "ics") {
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
        List<Error> errors = getErrors(resultObject, id)

        if (errors) {
            Response.ResponseBuilder responseBuilder = Response.status(Response.Status.BAD_REQUEST)
            return responseBuilder.entity(errors).build()
        }

        try {
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
                    //otherwise, update the instance
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
        } catch (Exception e) {
            logger.error("Exception while calling updateEvent", e)
            return internalServerError(ErrorMessages.unexpectedException).build()
        }
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
                                  String pathID = null) {
        List<Error> errors = []
        ResourceObject resourceObject
        Event event

        //if checking a PATCH request
        if (pathID) {
            if (!eventsDAO.getById(pathID)) {
                errors.add(new Error(
                        status: 404,
                        developerMessage: Resource.properties.get('notFound.developerMessage'),
                        userMessage: Resource.properties.get('notFound.userMessage'),
                        code: Integer.parseInt(Resource.properties.get('notFound.code')),
                        details: Resource.properties.get('notFound.details')
                ))
            }

            //ID in path and body should match
            if (resultObject.data.id != pathID) {
                errors.add(ErrorMessages.badRequest(ErrorMessages.mismatchID))
            }
        }

        //if checking a POST request
        if (resultObject.data.id && !pathID) {
            resultObject.data.id = resultObject.data.id.toString()

            if (!resultObject.data.id.matches(uuidRegEx)) {
                errors.add(ErrorMessages.conflict(ErrorMessages.invalidUUID))
            }
            if (eventsDAO.getById(resultObject.data.id)) {
                errors.add(ErrorMessages.conflict(ErrorMessages.idExists))
            }
        }

        //try casting ResultObject into ResourceObject and Event
        try {
            resourceObject = resultObject.data
            event = resultObject.data.attributes
        } catch (GroovyCastException e) {
            errors.add(ErrorMessages.badRequest(ErrorMessages.unknownFields))
            return errors
        }

        if (event.hashtag && event.hashtag.contains("#")) {
            errors.add(ErrorMessages.badRequest("Hashtag cannot contain '#'."))
        }

        if (event.location && !eventsDAO.checkPlace(event.location)) {
            errors.add(ErrorMessages.badRequest("Location could not be found."))
        }

        if (event.group && !eventsDAO.checkGroup(event.group)) {
            errors.add(ErrorMessages.badRequest("Group could not be found."))
        }

        if (event.department && !eventsDAO.checkDepartment(event.department)) {
            errors.add(ErrorMessages.badRequest("Department could not be found."))
        }

        event.customFields.each {
            if (!eventsDAO.checkCustomField(it.field.toString())) {
                errors.add(ErrorMessages.badRequest("Custom field '${it.field}' does not exist."))
            }
        }

        event.filters.each {
            String filterName = it.filter
            String filterID = eventsDAO.checkFilter(it.filter.toString())

            //check if filter exists
            if (!filterID) {
                errors.add(ErrorMessages.badRequest("Filter '${it.filter}' does not exist."))
            //if filter exists, check its items
            } else {
                it.items.each {
                    if (!eventsDAO.checkFilterItem(it, filterID)) {
                        errors.add(ErrorMessages.badRequest(
                                "Filter item '${it}' is not applicable to '"
                                + filterName + "'."))
                    }
                }
            }
        }

        event.instances.each {
            try {
                InstanceMapper.formatForDB(it.start.toString())
                InstanceMapper.formatForDB(it.end.toString())
            } catch (DateTimeParseException e) {
                errors.add(ErrorMessages.badRequest(
                        "Error with instance ID: ${it.id}. " +
                                ErrorMessages.parseDate
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
