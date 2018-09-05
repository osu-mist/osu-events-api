package edu.oregonstate.mist.osuevents.core

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import edu.oregonstate.mist.api.jsonapi.ResultObject
import groovy.transform.InheritConstructors

import java.time.ZonedDateTime

@JsonIgnoreProperties(ignoreUnknown=true)
class Event {
    @JsonIgnore
    String eventID

    String title
    String description

    String locationID
    String otherLocationName
    String room
    String address
    String city
    String state
    List<String> countyIDs
    String campusID

    String eventURL
    String photoURL

    String ticketURL
    String ticketCost

    String hashtag
    List<String> keywords
    List<String> tags

    Boolean allowsReviews
    Boolean allowUserActivity
    List<String> departmentIDs

    String contactName
    String contactEmail
    String contactPhone

    List<String> eventTypeIDs
    List<String> eventTopicIDs
    List<String> audienceIDs

    String visibility

    @JsonIgnore
    String owner

    List<Instance> instances = []

    static ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule())

    /**
     * Create new event from a ResultObject.
     * @param resultObject
     * @return
     */
    public static Event fromResultObject(ResultObject resultObject) {
        try {
            Event event = objectMapper.convertValue(resultObject.data['attributes'], Event.class)
            event.with {
                locationID = trimID(locationID)
                countyIDs = trimID(countyIDs)
                campusID = trimID(campusID)
                departmentIDs = trimID(departmentIDs)
                eventTypeIDs = trimID(eventTypeIDs)
                eventTopicIDs = trimID(eventTopicIDs)
                audienceIDs = trimID(audienceIDs)
            }
            event
        } catch (IllegalArgumentException e) {
            throw new EventException("Some fields weren't able to map to an event object.")
        } catch (NullPointerException e) {
            throw new EventException("Could not parse result object.")
        }
    }

    private static trimID(List<String> IDs) {
        IDs ? IDs.collect { it.trim() }.unique() : IDs
    }

    private static trimID(String ID) {
        ID ? ID.trim() : ID
    }

    public static final String unlistedVisibility = "Unlisted"
    public static final String restrictedVisibility = "Restricted"
    public static final String channelsVisibility = "Channels"

    public static List<String> validVisibilityValues = [
            unlistedVisibility,
            restrictedVisibility,
            channelsVisibility
    ]

    @JsonIgnore
    public Boolean isValidVisibility() {
        !this.visibility || validVisibilityValues.contains(this.visibility)
    }
}

@InheritConstructors
class EventException extends Exception {}

class Instance {
    @JsonFormat(shape=JsonFormat.Shape.STRING, timezone="UTC")
    ZonedDateTime start

    @JsonFormat(shape=JsonFormat.Shape.STRING, timezone="UTC")
    ZonedDateTime end
}