package edu.oregonstate.mist.osuevents.core

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import edu.oregonstate.mist.api.jsonapi.ResultObject
import groovy.transform.InheritConstructors

import java.time.ZonedDateTime

class Event {
    @JsonIgnore
    String eventID

    String title
    String description

    String locationID
    String room
    String address
    String city
    String state
    String county

    String eventURL
    String photoURL
    String facebookURL

    String ticketURL
    String ticketCost

    String hashtag
    List<String> keywords
    List<String> tags

    String groupID
    Boolean allowsReviews
    Boolean sponsored
    Boolean venuePageOnly
    Boolean excludeFromTrending
    Boolean allowUserActivity
    Boolean allowUserInterest
    List<String> departmentIDs

    String contactName
    String contactEmail
    String contactPhone

    List<String> eventTypeIDs
    List<String> eventTopicIDs
    List<String> audienceIDs

    @JsonIgnore
    String owner

    List<Instance> instances = []

    public static Event fromResultObject(ResultObject resultObject) {
        ObjectMapper objectMapper = new ObjectMapper()
        objectMapper.registerModule(new JavaTimeModule())

        try {
            objectMapper.convertValue(resultObject.data['attributes'], Event.class)
        } catch (IllegalArgumentException e) {
            throw new EventException("Some fields weren't able to map to an event object.")
        } catch (NullPointerException e) {
            throw new EventException("Could not parse result object.")
        }
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