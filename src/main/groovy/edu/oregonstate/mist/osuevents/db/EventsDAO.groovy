package edu.oregonstate.mist.osuevents.db

import edu.oregonstate.mist.osuevents.core.Event
import edu.oregonstate.mist.osuevents.core.Instance
import edu.oregonstate.mist.osuevents.mapper.EventMapper
import edu.oregonstate.mist.osuevents.mapper.InstanceMapper
import org.skife.jdbi.v2.sqlobject.Bind
import org.skife.jdbi.v2.sqlobject.SqlQuery
import org.skife.jdbi.v2.sqlobject.SqlUpdate
import org.skife.jdbi.v2.sqlobject.customizers.Mapper

public interface EventsDAO extends Closeable {

    /**
     * GET by ID
     */
    @SqlQuery("""
        select
            EVENTS.EVENT_ID,
            EVENTS.TITLE,
            EVENTS.DESCRIPTION,
            PLACES.NAME as PLACE_NAME,
            EVENTS.ROOM,
            EVENTS.ADDRESS,
            EVENTS.CITY,
            EVENTS.STATE,
            EVENTS.EVENT_URL,
            EVENTS.PHOTO_URL,
            EVENTS.TICKET_URL,
            EVENTS.FACEBOOK_URL,
            EVENTS.COST,
            EVENTS.HASHTAG,
            EVENTS.KEYWORDS,
            EVENTS.TAGS,
            GROUPS.NAME as GROUP_NAME,
            DEPARTMENTS.NAME as DEPARTMENT_NAME,
            EVENTS.ALLOWS_REVIEWS,
            EVENTS.SPONSORED,
            EVENTS.VENUE_PAGE_ONLY,
            EVENTS.EXCLUDE_FROM_TRENDING,
            EVENTS.VISIBILITY,
            EVENTS.FILTERS,
            EVENTS.CUSTOM_FIELDS
        from EVENTS
        left join PLACES
          on EVENTS.PLACE_ID = PLACES.PLACE_ID
        left join GROUPS
          on EVENTS.GROUP_ID = GROUPS.GROUP_ID
        left join DEPARTMENTS
          ON EVENTS.DEPARTMENT_ID = DEPARTMENTS.DEPARTMENT_ID
        where EVENT_ID=:id
        """)
    @Mapper(EventMapper)
    Event getById(@Bind("id") String id)

    @SqlQuery("""
        select
            CLIENT_INSTANCE_ID,
            TO_CHAR(START_TIME, 'yyyy-mm-dd hh24:mi:ss') AS START_TIME,
            TO_CHAR(END_TIME, 'yyyy-mm-dd hh24:mi:ss') AS END_TIME
        FROM INSTANCES
        WHERE EVENT_ID=:id
        """)
    @Mapper(InstanceMapper)
    List<Instance> getInstances(@Bind("id") String id)

    /**
     * GET all events
     */
    @SqlQuery("""
        select
            EVENTS.EVENT_ID,
            EVENTS.TITLE,
            EVENTS.DESCRIPTION,
            PLACES.NAME as PLACE_NAME,
            EVENTS.ROOM,
            EVENTS.ADDRESS,
            EVENTS.CITY,
            EVENTS.STATE,
            EVENTS.EVENT_URL,
            EVENTS.PHOTO_URL,
            EVENTS.TICKET_URL,
            EVENTS.FACEBOOK_URL,
            EVENTS.COST,
            EVENTS.HASHTAG,
            EVENTS.KEYWORDS,
            EVENTS.TAGS,
            GROUPS.NAME as GROUP_NAME,
            DEPARTMENTS.NAME as DEPARTMENT_NAME,
            EVENTS.ALLOWS_REVIEWS,
            EVENTS.SPONSORED,
            EVENTS.VENUE_PAGE_ONLY,
            EVENTS.EXCLUDE_FROM_TRENDING,
            EVENTS.VISIBILITY,
            EVENTS.FILTERS,
            EVENTS.CUSTOM_FIELDS
        from EVENTS
        left join PLACES
          on EVENTS.PLACE_ID = PLACES.PLACE_ID
        left join GROUPS
          on EVENTS.GROUP_ID = GROUPS.GROUP_ID
        left join DEPARTMENTS
          ON EVENTS.DEPARTMENT_ID = DEPARTMENTS.DEPARTMENT_ID
        """)
    @Mapper(EventMapper)
    List<Event> getEvents()

    /**
     * POST a new event
     */
    @SqlUpdate("""
        INSERT INTO EVENTS (EVENT_ID, TITLE, DESCRIPTION, PLACE_ID, GROUP_ID, DEPARTMENT_ID,
            ROOM, ADDRESS, CITY, STATE, EVENT_URL, PHOTO_URL, TICKET_URL, FACEBOOK_URL, COST,
            HASHTAG, KEYWORDS, TAGS, ALLOWS_REVIEWS, SPONSORED, VENUE_PAGE_ONLY,
            EXCLUDE_FROM_TRENDING, VISIBILITY, CREATED_AT)
        VALUES (:event_id,
            :event_title,
            :description,
            (SELECT PLACE_ID FROM PLACES
                WHERE NAME = :location),
            (SELECT GROUP_ID FROM GROUPS
                WHERE NAME = :group_name
                OR PAGE_NAME = :group_name),
            (SELECT DEPARTMENT_ID FROM DEPARTMENTS
                WHERE NAME = :department),
            :room,
            :address,
            :city,
            :state,
            :event_url,
            :photo_url,
            :ticket_url,
            :facebook_url,
            :cost,
            :hashtag,
            :keywords,
            :tags,
            :allows_reviews,
            :sponsored,
            :venue_page_only,
            :exclude_from_trending,
            :visibility,
            SYSDATE)
        """)
//    :filters,
//    :custom_fields,
//    SYSDATE)
//    , FILTERS, CUSTOM_FIELDS, CREATED_AT)
    void createEvent(@Bind("event_id") String eventID,
                     @Bind("event_title") String title,
                     @Bind("description") String description,
                     @Bind("location") String location,
                     @Bind("group_name") String group,
                     @Bind("department") String department,
                     @Bind("room") String room,
                     @Bind("address") String address,
                     @Bind("city") String city,
                     @Bind("state") String state,
                     @Bind("event_url") String eventURL,
                     @Bind("photo_url") String photoURL,
                     @Bind("ticket_url") String ticketURL,
                     @Bind("facebook_url") String facebookURL,
                     @Bind("cost") String cost,
                     @Bind("hashtag") String hashtag,
                     @Bind("keywords") String keywords,
                     @Bind("tags") String tags,
                     @Bind("allows_reviews") Boolean allowsReviews,
                     @Bind("sponsored") Boolean sponsored,
                     @Bind("venue_page_only") Boolean venuePageOnly,
                     @Bind("exclude_from_trending") Boolean excludeFromTrending,
                     @Bind("visibility") String visibility)
//                     @Bind("filters") String filters,
//                     @Bind("custom_fields") String custom_fields)

    @SqlUpdate("""
        INSERT INTO INSTANCES (INSTANCE_ID, CLIENT_INSTANCE_ID, EVENT_ID, START_TIME, END_TIME)
        VALUES (instance_seq.NEXTVAL,
            :client_instance_id,
            :event_id,
            TO_DATE(:start_date, 'yyyy-mm-dd hh24:mi:ss'),
            TO_DATE(:end_date, 'yyyy-mm-dd hh24:mi:ss'))
        """)
    void createInstance(@Bind("client_instance_id") String instanceID,
                        @Bind("event_id") String eventID,
                        @Bind("start_date") String start,
                        @Bind("end_date") String end)
}