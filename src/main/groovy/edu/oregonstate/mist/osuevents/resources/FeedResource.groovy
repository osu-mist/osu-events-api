package edu.oregonstate.mist.osuevents.resources

import com.codahale.metrics.annotation.Timed
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.dataformat.csv.CsvMapper
import com.fasterxml.jackson.dataformat.csv.CsvSchema
import edu.oregonstate.mist.api.Resource
import edu.oregonstate.mist.osuevents.core.Campus
import edu.oregonstate.mist.osuevents.core.Department
import edu.oregonstate.mist.osuevents.core.Event
import edu.oregonstate.mist.osuevents.core.Location
import edu.oregonstate.mist.osuevents.db.EventsDAOWrapper
import edu.oregonstate.mist.osuevents.db.Filter
import edu.oregonstate.mist.osuevents.db.Filters
import edu.oregonstate.mist.osuevents.db.LocalistDAO
import groovy.transform.TypeChecked

import javax.annotation.security.PermitAll
import javax.ws.rs.GET
import javax.ws.rs.QueryParam
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.Response
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Path('/calendar/feed')
@Produces(["application/json", "text/csv"])
@PermitAll
@TypeChecked
class FeedResource extends Resource {

    private final EventsDAOWrapper eventsDAOWrapper
    private final LocalistDAO localistDAO

    private final String defaultTimezone
    private final String exceptionTimezone
    private final String exceptionTimezoneCampusID

    FeedResource(EventsDAOWrapper eventsDAOWrapper,
                 LocalistDAO localistDAO,
                 String defaultTimezone,
                 String exceptionTimezone,
                 String exceptionTimezoneCampusID) {
        this.eventsDAOWrapper = eventsDAOWrapper
        this.localistDAO = localistDAO
        this.defaultTimezone = defaultTimezone
        this.exceptionTimezone = exceptionTimezone
        this.exceptionTimezoneCampusID = exceptionTimezoneCampusID

    }

    /**
     * Get a CSV list of events formatted for localist.
     * @return
     */
    @GET
    @Timed
    Response getFeed(@QueryParam("changedInPastHours") Integer changedInPastHours) {
        CsvMapper mapper = new CsvMapper()
        CsvSchema schema = mapper.schemaFor(FeedEvent.class).withHeader()

	   if (changedInPastHours != null && changedInPastHours <= 0) {
                //A null value is valid (returns all events), but negative and zero values are not
                return badRequest("changedInPastHours must be a positive, non-zero value").build()
	   }

        List<Event> events = eventsDAOWrapper.getEvents(changedInPastHours)

        List<FeedEvent> feedEvents = []

        def locations = [:]
        def departments = [:]
        def campuses = [:]
        Filters filters = localistDAO.getFilters()

        events.each { event ->
            event.instances.each { instance ->
                FeedEvent feedEvent = new FeedEvent(
                        eventID: event.eventID,
                        title: event.title,
                        description: event.description,
                        room: event.room,
                        address: event.address,
                        city: event.city,
                        state: event.state,
                        eventURL: event.eventURL,
                        photoURL: event.photoURL,
                        ticketURL: event.ticketURL,
                        ticketCost: event.ticketCost,
                        hashtag: event.hashtag,
                        contactName: event.contactName,
                        contactEmail: event.contactEmail,
                        contactPhone: event.contactPhone,
                        visibility: event.visibility
                )

                feedEvent.with {
                    setAllowsReviews(event.allowsReviews)
                    setAllowUserActivity(event.allowUserActivity)
                    setKeywords(event.keywords)
                    setTags(event.tags)

                    setCounties(getFilterNameListFromIDList(
                            event.countyIDs, filters.counties))
                    setEventTypes(getFilterNameListFromIDList(
                            event.eventTypeIDs, filters.eventTypes))
                    setEventTopics(getFilterNameListFromIDList(
                            event.eventTopicIDs, filters.eventTopics))
                    setAudiences(getFilterNameListFromIDList(
                            event.audienceIDs, filters.audiences))

                    Location location
                    if (event.locationID) {
                        if (locations[event.locationID]) {
                            feedEvent.location = locations[event.locationID]
                        } else {
                            location = localistDAO.getlocationByID(event.locationID)

                            // if we can't find the location name,
                            // use the ID as a backup/for debugging
                            String locationName = location ? location.name : event.locationID
                            locations[event.locationID] = feedEvent.location = locationName
                        }
                    } else if (event.otherLocationName) {
                        feedEvent.location = event.otherLocationName
                    }

                    if (location && (location.campusID == exceptionTimezoneCampusID)) {
                        timeZone = exceptionTimezone
                    } else if (event.campusID == exceptionTimezoneCampusID) {
                        timeZone = exceptionTimezone
                    } else {
                        timeZone = defaultTimezone
                    }

                    setStartDate(instance.start)
                    setStartTime(instance.start)
                    setEndDate(instance.end)
                    setEndTime(instance.end)

                    if (event.departmentIDs) {
                        List<String> departmentNames = []

                        event.departmentIDs.each { departmentID ->
                            if (departments[departmentID]) {
                                departmentNames.add((String) departments[departmentID])
                            } else {
                                Department department = localistDAO.getDepartmentByID(departmentID)

                                // if we can't find the department name,
                                // use the ID as a backup/for debugging
                                String departmentName = department ? department.name : departmentID
                                departments[departmentID] = departmentName
                                departmentNames.add(departmentName)
                            }
                        }
                        setDepartments(departmentNames)
                    }

                    if (event.campusID) {
                        if (campuses[event.campusID]) {
                            campus = campuses[event.campusID]
                        } else {
                            Campus campus = localistDAO.getCampusByID(event.campusID)

                            // if we can't find the campus name,
                            // use the ID as a backup/for debugging
                            String campusName = campus ? campus.name : event.campusID
                            campuses[event.campusID] = feedEvent.campus = campusName
                        }
                    }
                }
                feedEvents.add(feedEvent)
            }
        }

        String csv
        try {
            csv = mapper.writer(schema).writeValueAsString(feedEvents)
        } catch (JsonMappingException e) {
            println(e.toString())
        }

        Response.ok(csv).build()
    }

    /**
     * Helper method to return a list of filter names given their ID's.
     * @param idList
     * @param filters
     * @return
     */
    private List<String> getFilterNameListFromIDList(List<String> idList, List<Filter> filters) {
        idList.collect { id ->
            Filter filter

            filter = filters.find {
                it.filterID == id
            }

            if (filter) {
                filter.name
            } else {
                // if we can't find the filter name, use the ID as a backup/for debugging
                id
            }
        }
    }
}

class FeedEvent {
    @JsonIgnore
    String timeZone

    //Fields and names gathered from https://support.localist.com/bulk-add/

    @JsonProperty("External ID")
    String eventID
    @JsonProperty("Title")
    String title
    @JsonProperty("Description")
    String description

    @JsonProperty("Date From")
    String startDate
    @JsonIgnore
    void setStartDate(ZonedDateTime zonedStartDate) {
        this.startDate = zonedStartDate.format(dateFormat())
    }

    @JsonProperty("Date To")
    String endDate
    @JsonIgnore
    void setEndDate(ZonedDateTime zonedEndDate) {
        this.endDate = zonedEndDate.format(dateFormat())
    }

    @JsonProperty("Start Time")
    String startTime
    @JsonIgnore
    void setStartTime(ZonedDateTime zonedStartDate) {
        this.startTime = zonedStartDate.format(timeFormat())
    }

    @JsonProperty("End Time")
    String endTime
    @JsonIgnore
    void setEndTime(ZonedDateTime zonedEndDate) {
        this.endTime = zonedEndDate.format(timeFormat())
    }

    private DateTimeFormatter dateFormat() {
        DateTimeFormatter.ofPattern("MM/dd/yyyy")
                .withZone(ZoneId.of(this.timeZone))
    }

    private DateTimeFormatter timeFormat() {
        DateTimeFormatter.ofPattern("HH:mm")
                .withZone(ZoneId.of(this.timeZone))
    }

    @JsonProperty("Location")
    String location
    @JsonProperty("Room")
    String room
    @JsonProperty("Address")
    String address
    @JsonProperty("City")
    String city
    @JsonProperty("State")
    String state

    @JsonProperty("County")
    String counties
    @JsonIgnore
    void setCounties(List<String> counties) {
        this.counties = joinList(counties)
    }

    @JsonProperty("Community")
    String campus

    @JsonProperty("Event Website")
    String eventURL
    @JsonProperty("Photo URL")
    String photoURL
    @JsonProperty("Ticket URL")
    String ticketURL
    @JsonProperty("Cost")
    String ticketCost

    @JsonProperty("Keywords")
    String keywords
    @JsonIgnore
    void setKeywords(List<String> keywordList) {
        this.keywords = joinList(keywordList)
    }

    @JsonProperty("Tags")
    String tags
    @JsonIgnore
    void setTags(List<String> taglist) {
        this.tags = joinList(taglist)
    }

    @JsonProperty("Hashtag")
    String hashtag

    @JsonProperty("Department")
    String departments
    @JsonIgnore
    void setDepartments(List<String> departments) {
        this.departments = joinList(departments)
    }

    @JsonProperty("Allows Reviews")
    String allowsReviews
    @JsonIgnore
    void setAllowsReviews(Boolean allowsReviews) {
        this.allowsReviews = parseBoolean(allowsReviews)
    }

    @JsonProperty("Allow User Activity")
    String allowUserActivity
    @JsonIgnore
    void setAllowUserActivity(Boolean allowUserActivity) {
        this.allowUserActivity = parseBoolean(allowUserActivity)
    }

    private static String parseBoolean(Boolean bool) {
        bool ? "1" : null
    }

    @JsonProperty("Contact name for event questions or disability accommodations")
    String contactName
    @JsonProperty("Contact Email")
    String contactEmail
    @JsonProperty("Contact Phone")
    String contactPhone

    @JsonProperty("Event Types")
    String eventTypes
    @JsonIgnore
    void setEventTypes(List<String> eventTypes) {
        this.eventTypes = joinList(eventTypes)
    }

    @JsonProperty("Event Topic")
    String eventTopics
    @JsonIgnore
    void setEventTopics(List<String> eventTopics) {
        this.eventTopics = joinList(eventTopics)
    }

    @JsonProperty("Audience")
    String audiences
    @JsonIgnore
    void setAudiences(List<String> audiences) {
        this.audiences = joinList(audiences)
    }

    private static String joinList(List<String> list) {
        if (list) {
            list.join(", ")
        } else {
            null
        }
    }

    @JsonProperty("Visibility")
    String visibility
}
