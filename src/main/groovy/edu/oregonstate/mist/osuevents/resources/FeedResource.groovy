package edu.oregonstate.mist.osuevents.resources

import com.codahale.metrics.annotation.Timed
import com.fasterxml.jackson.annotation.JsonFormat
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
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.Response
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Path('/calendar/feed')
@Produces("text/csv")
@PermitAll
@TypeChecked
class FeedResource extends Resource {

    private final EventsDAOWrapper eventsDAOWrapper
    private final LocalistDAO localistDAO

    FeedResource(EventsDAOWrapper eventsDAOWrapper, LocalistDAO localistDAO) {
        this.eventsDAOWrapper = eventsDAOWrapper
        this.localistDAO = localistDAO
    }

    @GET
    @Timed
    Response getFeed() {
        CsvMapper mapper = new CsvMapper()
        CsvSchema schema = mapper.schemaFor(FeedEvent.class).withHeader()

        List<Event> events = eventsDAOWrapper.getEvents()

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
                        startDate: instance.start.format(FeedEvent.dateFormat),
                        endDate: instance.end.format(FeedEvent.dateFormat),
                        startTime: instance.start.format(FeedEvent.timeFormat),
                        endTime: instance.end.format(FeedEvent.timeFormat),
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

                feedEvent.setAllowsReviews(event.allowsReviews)
                feedEvent.setAllowUserActivity(event.allowUserActivity)
                feedEvent.setKeywords(event.keywords)
                feedEvent.setTags(event.tags)

                feedEvent.setCounties(getFilterNameListFromIDList(
                        event.countyIDs, filters.counties))
                feedEvent.setEventTypes(getFilterNameListFromIDList(
                        event.eventTypeIDs, filters.eventTypes))
                feedEvent.setEventTopics(getFilterNameListFromIDList(
                        event.eventTopicIDs, filters.eventTopics))
                feedEvent.setAudiences(getFilterNameListFromIDList(
                        event.audienceIDs, filters.audiences))

                if (event.locationID) {
                    if (locations[event.locationID]) {
                        feedEvent.location = locations[event.locationID]
                    } else {
                        Location location = localistDAO.getlocationByID(event.locationID)

                        if (location) {
                            locations[event.locationID] = location.name
                            feedEvent.location = location.name
                        } else {
                            // if we can't find the location name,
                            // use the ID as a backup/for debugging
                            feedEvent.location = event.locationID
                        }
                    }
                } else if (event.otherLocationName) {
                    feedEvent.location = event.otherLocationName
                }

                if (event.departmentIDs) {
                    List<String> departmentNames = []

                    event.departmentIDs.each { departmentID ->
                        if (departments[departmentID]) {
                            departmentNames.add((String)departments[departmentID])
                        } else {
                            Department department = localistDAO.getDepartmentByID(departmentID)
                            if (department) {
                                departments[departmentID] = department.name
                                departmentNames.add(department.name)
                            } else {
                                // if we can't find the department name,
                                // use the ID as a backup/for debugging
                                departmentNames.add(departmentID)
                            }
                        }
                    }
                    feedEvent.setDepartments(departmentNames)
                }

                if (event.campusID) {
                    if (campuses[event.campusID]) {
                        feedEvent.campus = campuses[event.campusID]
                    } else {
                        Campus campus = localistDAO.getCampusByID(event.campusID)

                        if (campus) {
                            campuses[event.campusID] = campus.name
                            feedEvent.campus = campus.name
                        } else {
                            // if we can't find the location name,
                            // use the ID as a backup/for debugging
                            feedEvent.campus = event.campusID
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
    static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy")
            .withZone(ZoneId.of("America/Los_Angeles"))
    static final DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm")
            .withZone(ZoneId.of("America/Los_Angeles"))

    //Fields and names gathered from https://support.localist.com/bulk-add/

    @JsonProperty("External ID")
    String eventID
    @JsonProperty("Title")
    String title
    @JsonProperty("Description")
    String description

    @JsonProperty("Date From")
    String startDate
    @JsonProperty("Date To")
    String endDate
    @JsonProperty("Start Time")
    String startTime
    @JsonProperty("End Time")
    String endTime

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
