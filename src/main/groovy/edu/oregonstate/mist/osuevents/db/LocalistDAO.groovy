package edu.oregonstate.mist.osuevents.db

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import edu.oregonstate.mist.osuevents.core.Campus
import edu.oregonstate.mist.osuevents.core.Location
import edu.oregonstate.mist.osuevents.core.PaginatedCampuses
import edu.oregonstate.mist.osuevents.core.PaginatedLocations
import edu.oregonstate.mist.osuevents.core.PagniatedDepartments
import edu.oregonstate.mist.osuevents.core.SimpleFilterObject
import org.apache.http.HttpResponse
import org.apache.http.HttpStatus
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.util.EntityUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.ws.rs.core.UriBuilder

/**
 * DAO layer for interacting with localist via its REST API
 */
class LocalistDAO {
    HttpClient httpClient
    URI baseURI

    static String localistApiVersion = "2.2"

    static String filtersEndpoint = "/events/filters"
    static String placesEndpoint = "/places"
    static String departmentsEndpoint = "/departments"
    String communitiesEndpoint // set in constructor as config param, depends on organizationID

    ObjectMapper objectMapper = new ObjectMapper()

    private static Logger logger = LoggerFactory.getLogger(this)

    LocalistDAO(HttpClient httpClient, String baseURL, String organizationID) {
        this.httpClient = httpClient
        this.baseURI = UriBuilder.fromUri(baseURL.toURI())
                .path("/api/${localistApiVersion}").build()
        this.communitiesEndpoint = "/organizations/${organizationID}/communities"
    }

    /**
     * Get event topics from filters.
     * @return
     */
    List<SimpleFilterObject> getEventTopics() {
        getFilters().eventTopics.collect { createSimpleFilterObjectIfExists(it) }
    }

    /**
     * Get single event topic by ID.
     * @param id
     * @return
     */
    SimpleFilterObject getEventTopicByID(String id) {
        Filter filter = getFilters().eventTopics.find { it.filterID == id }
        createSimpleFilterObjectIfExists(filter)
    }

    /**
     * Get event types from filters.
     * @return
     */
    List<SimpleFilterObject> getEventTypes() {
        getFilters().eventTypes.collect { createSimpleFilterObjectIfExists(it) }
    }

    /**
     * Get single event type by ID.
     * @param id
     * @return
     */
    SimpleFilterObject getEventTypeByID(String id) {
        Filter filter = getFilters().eventTypes.find { it.filterID == id }
        createSimpleFilterObjectIfExists(filter)
    }

    /**
     * Get audiences from filters.
     * @return
     */
    List<SimpleFilterObject> getAudiences() {
        getFilters().audiences.collect { createSimpleFilterObjectIfExists(it) }
    }

    /**
     * Get single audience by ID.
     * @param id
     * @return
     */
    SimpleFilterObject getAudienceByID(String id) {
        Filter filter = getFilters().audiences.find { it.filterID == id }
        createSimpleFilterObjectIfExists(filter)
    }

    /**
     * Get counties from filters.
     * @return
     */
    List<SimpleFilterObject> getCounties() {
        getFilters().counties.collect { createSimpleFilterObjectIfExists(it) }
    }

    /**
     * Get single county by ID.
     * @param id
     * @return
     */
    SimpleFilterObject getCountyByID(String id) {
        Filter filter = getFilters().counties.find { it.filterID == id }
        createSimpleFilterObjectIfExists(filter)

    }

    private SimpleFilterObject createSimpleFilterObjectIfExists(Filter filter) {
        if (filter) {
            SimpleFilterObject.fromFilter(filter)
        } else {
            null
        }
    }

    /**
     * Get multiple campuses as well as pagination information.
     * @param pageNumber
     * @param pageSize
     * @return
     */
    PaginatedCampuses getCampuses(Integer pageNumber, Integer pageSize) {
        HttpResponse response = getResponse(communitiesEndpoint, pageNumber, pageSize)

        String responseEntity = EntityUtils.toString(response.entity)

        MultipleCommunitiesResponse communities = objectMapper.readValue(
                responseEntity, MultipleCommunitiesResponse)

        new PaginatedCampuses(
                campuses: communities.communities.collect {
                    Campus.fromCommunity(it.community)
                },
                paginationObject: communities.paginationObject
        )
    }

    /**
     * Get single campus by ID.
     * @param id
     * @return
     */
    Campus getCampusByID(String id) {
        HttpResponse response = getResponse("${communitiesEndpoint}/${id}")

        if (response.statusLine.statusCode == HttpStatus.SC_NOT_FOUND) {
            EntityUtils.consumeQuietly(response.entity)
            null
        } else {
            String responseEntity = EntityUtils.toString(response.entity)

            SingleCommunityResponse community = objectMapper.readValue(
                    responseEntity, SingleCommunityResponse)

            Campus.fromCommunity(community.community)
        }
    }

    /**
     * Get multiple locations as well as pagination information.
     * @param pageNumber
     * @param pageSize
     * @return
     */
    PaginatedLocations getLocations(Integer pageNumber, Integer pageSize) {
        HttpResponse response = getResponse(placesEndpoint, pageNumber, pageSize)

        String responseEntity = EntityUtils.toString(response.entity)

        MultiplePlacesResponse places = objectMapper.readValue(
                responseEntity, MultiplePlacesResponse)

        new PaginatedLocations(
                locations: places.places.collect {
                    Location.fromPlace(it.place)
                },
                paginationObject: places.paginationObject
        )
    }

    /**
     * Get single location by ID.
     * @param id
     * @return
     */
    Location getlocationByID(String id) {
        HttpResponse response = getResponse("${placesEndpoint}/${id}")

        if (response.statusLine.statusCode == HttpStatus.SC_NOT_FOUND) {
            EntityUtils.consumeQuietly(response.entity)
            null
        } else {
            String responseEntity = EntityUtils.toString(response.entity)

            SinglePlaceResponse place = objectMapper.readValue(
                    responseEntity, SinglePlaceResponse)

            Location.fromPlace(place.place)
        }
    }

    /**
     * Get multiple departments as well as pagination information.
     * @param pageNumber
     * @param pageSize
     * @return
     */
    PagniatedDepartments getDepartments(Integer pageNumber, Integer pageSize) {
        HttpResponse response = getResponse(departmentsEndpoint, pageNumber, pageSize)

        String responseEntity = EntityUtils.toString(response.entity)

        MultipleDepartmentsResponse departments = objectMapper.readValue(
                responseEntity, MultipleDepartmentsResponse)

        new PagniatedDepartments(
                departments: departments.departments.collect {
                    edu.oregonstate.mist.osuevents.core.Department.fromLocalistDepartment(
                            it.department)
                },
                paginationObject: departments.paginationObject
        )
    }

    /**
     * Get multiple departments as well as pagination information.
     * @param pageNumber
     * @param pageSize
     * @return
     */
    edu.oregonstate.mist.osuevents.core.Department getDepartmentByID(String id) {
        HttpResponse response = getResponse("${departmentsEndpoint}/${id}")

        if (response.statusLine.statusCode == HttpStatus.SC_NOT_FOUND) {
            EntityUtils.consumeQuietly(response.entity)
            null
        } else {
            String responseEntity = EntityUtils.toString(response.entity)

            SingleDepartmentResponse department = objectMapper.readValue(
                    responseEntity, SingleDepartmentResponse)

            edu.oregonstate.mist.osuevents.core.Department.fromLocalistDepartment(
                    department.department)
        }
    }

    /**
     * Get all filters, including event types, event topics, audiences, and counties.
     * @return
     */
    Filters getFilters() {
        HttpResponse response = getResponse(filtersEndpoint)

        String responseEntity = EntityUtils.toString(response.entity)

        objectMapper.readValue(responseEntity, Filters)
    }

    /**
     * Sends HTTP GET request. Accepts pagination parameters.
     * @param endpoint
     * @param pageNumber
     * @param pageSize
     * @return
     */
    private HttpResponse getResponse(String endpoint,
                                     Integer pageNumber = null,
                                     Integer pageSize = null) {
        UriBuilder uriBuilder = UriBuilder.fromUri(baseURI)
        uriBuilder.path(endpoint)

        if (pageNumber != null) {
            uriBuilder.queryParam("page", pageNumber)
        }

        if (pageSize != null) {
            uriBuilder.queryParam("pp", pageSize)
        }

        URI requestURI = uriBuilder.build()

        logger.info("Making a request to ${requestURI}")

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
class MultipleCommunitiesResponse {
    List<SingleCommunityResponse> communities

    @JsonProperty("page")
    PaginationObject paginationObject
}

@JsonIgnoreProperties(ignoreUnknown=true)
class SingleCommunityResponse {
    Community community
}

@JsonIgnoreProperties(ignoreUnknown=true)
class Community {
    String id
    String name

    @JsonProperty("time_zone")
    String timeZone

    @JsonProperty("localist_url")
    String calendarURL
}

@JsonIgnoreProperties(ignoreUnknown=true)
class MultiplePlacesResponse {
    List<SinglePlaceResponse> places

    @JsonProperty("page")
    PaginationObject paginationObject
}

@JsonIgnoreProperties(ignoreUnknown=true)
class SinglePlaceResponse {
    Place place
}

@JsonIgnoreProperties(ignoreUnknown=true)
class Place {
    String id
    String name

    @JsonProperty("campus_id")
    String campusID

    String latitude
    String longitude
    String street
    String city
    String state
    String zip

    @JsonProperty("localist_url")
    String calendarURL
    String url

    @JsonProperty("photo_url")
    String photoURL

    @JsonProperty("geo")
    private void unpackGeo(Map<String, String> geo) {
        this.latitude = geo.get("latitude")
        this.longitude = geo.get("longitude")
        this.street = geo.get("street")
        this.city = geo.get("city")
        this.state = geo.get("state")
        this.zip = geo.get("zip")
    }
}

@JsonIgnoreProperties(ignoreUnknown=true)
class MultipleDepartmentsResponse {
    List<SingleDepartmentResponse> departments

    @JsonProperty("page")
    PaginationObject paginationObject
}

@JsonIgnoreProperties(ignoreUnknown=true)
class SingleDepartmentResponse {
    Department department
}

@JsonIgnoreProperties(ignoreUnknown=true)
class Department {
    String id
    String name

    @JsonProperty("campus_id")
    String campusID

    @JsonProperty("localist_url")
    String calendarURL
    String url

    @JsonProperty("description_text")
    String description
}

@JsonIgnoreProperties(ignoreUnknown=true)
class PaginationObject {
    Integer current
    Integer size
    Integer total
}