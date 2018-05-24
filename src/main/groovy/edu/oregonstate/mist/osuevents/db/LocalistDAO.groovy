package edu.oregonstate.mist.osuevents.db

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import edu.oregonstate.mist.osuevents.core.Audience
import edu.oregonstate.mist.osuevents.core.Campus
import edu.oregonstate.mist.osuevents.core.County
import edu.oregonstate.mist.osuevents.core.EventTopic
import edu.oregonstate.mist.osuevents.core.EventType
import org.apache.http.HttpResponse
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.util.EntityUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.ws.rs.core.UriBuilder

class LocalistDAO {
    HttpClient httpClient
    URI baseURI
    String organizationID //this API only handles one organization

    String localistApiVersion = "2.1"

    ObjectMapper objectMapper = new ObjectMapper()

    private static Logger logger = LoggerFactory.getLogger(this)

    LocalistDAO(HttpClient httpClient, String baseURL, String organizationID) {
        this.httpClient = httpClient
        this.baseURI = UriBuilder.fromUri(baseURL.toURI())
                .path("/api/${localistApiVersion}").build()
        this.organizationID = organizationID
    }

    List<EventTopic> getEventTopics() {
        Filters filters = getFilters()

        filters.eventTopics.collect { EventTopic.fromFilter(it) }
    }

    EventTopic getEventTopicByID(String id) {
        Filter filter = getFilters().eventTopics.find { it.filterID == id }

        if (filter) {
            EventTopic.fromFilter(filter)
        } else {
            null
        }
    }

    List<EventType> getEventTypes() {
        Filters filters = getFilters()

        filters.eventTypes.collect { EventType.fromFilter(it) }
    }

    EventType getEventTypeByID(String id) {
        Filter filter = getFilters().eventTypes.find { it.filterID == id }

        if (filter) {
            EventType.fromFilter(filter)
        } else {
            null
        }
    }

    List<Audience> getAudiences() {
        Filters filters = getFilters()

        filters.audiences.collect { Audience.fromFilter(it) }
    }

    Audience getAudienceByID(String id) {
        Filter filter = getFilters().audiences.find { it.filterID == id }

        if (filter) {
            Audience.fromFilter(filter)
        } else {
            null
        }
    }

    List<County> getCounties() {
        Filters filters = getFilters()

        filters.counties.collect { County.fromFilter(it) }
    }

    County getCountyByID(String id) {
        Filter filter = getFilters().counties.find { it.filterID == id }

        if (filter) {
            County.fromFilter(filter)
        } else {
            null
        }
    }

    List<Campus> getCampuses() {
        //todo: implement this
    }

    Campus getCampusByID(String ID) {
        //todo: implement this
    }

    private Filters getFilters() {
        HttpResponse response = getResponse("/events/filters")

        String responseEntity = EntityUtils.toString(response.entity)

        objectMapper.readValue(responseEntity, Filters)
    }

    private HttpResponse getResponse(String endpoint) {
        URI requestURI = UriBuilder.fromUri(baseURI).path(endpoint).build()

        httpClient.execute(new HttpGet(requestURI))
    }

}

@JsonIgnoreProperties(ignoreUnknown=true)
class Filters {
    @JsonProperty("event_counties")
    List<Filter> counties

    @JsonProperty("event_audience")
    List<Filter> audiences

    @JsonProperty("event_types")
    List<Filter> eventTypes

    @JsonProperty("event_event_topic")
    List<Filter> eventTopics
}

@JsonIgnoreProperties(ignoreUnknown=true)
class Filter {
    @JsonProperty("id")
    String filterID

    String name

    @JsonProperty("parent_id")
    String parentID
}

@JsonIgnoreProperties(ignoreUnknown=true)
class Community {
    String id
    String name
    String calendarURL
}