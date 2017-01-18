package edu.oregonstate.mist.osuevents.db

import edu.oregonstate.mist.api.jsonapi.ResourceObject
import edu.oregonstate.mist.osuevents.core.Event
import edu.oregonstate.mist.osuevents.core.Instance
import edu.oregonstate.mist.osuevents.mapper.EventMapper
import edu.oregonstate.mist.osuevents.mapper.InstanceMapper
import org.skife.jdbi.v2.sqlobject.Bind
import org.skife.jdbi.v2.sqlobject.BindBean
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
        and EVENTS.DELETED_AT IS NULL
        """)
    @Mapper(EventMapper)
    ResourceObject getById(@Bind("id") String id)

    @SqlQuery("""
        select
            CLIENT_INSTANCE_ID,
            TO_CHAR(START_TIME, 'yyyy-mm-dd hh24:mi:ss') AS START_TIME,
            TO_CHAR(END_TIME, 'yyyy-mm-dd hh24:mi:ss') AS END_TIME
        FROM INSTANCES
        WHERE EVENT_ID=:id
        AND START_TIME IS NOT NULL
        AND END_TIME IS NOT NULL
        """)
    @Mapper(InstanceMapper)
    List<Instance> getInstances(@Bind("id") String id)

    @SqlQuery("""
        select
            CLIENT_INSTANCE_ID,
            TO_CHAR(START_TIME, 'yyyy-mm-dd hh24:mi:ss') AS START_TIME,
            TO_CHAR(END_TIME, 'yyyy-mm-dd hh24:mi:ss') AS END_TIME
        FROM INSTANCES
        WHERE EVENT_ID=:event_id
        AND CLIENT_INSTANCE_ID=:instance_id
        AND START_TIME IS NOT NULL
        AND END_TIME IS NOT NULL
        """)
    @Mapper(InstanceMapper)
    Instance getInstance(@Bind("event_id") String eventID,
                         @Bind("instance_id") String instanceID)

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
        where EVENTS.DELETED_AT IS NULL
        """)
    @Mapper(EventMapper)
    List<ResourceObject> getEvents()

    /**
     * POST a new event
     */
    @SqlUpdate("""
        INSERT INTO EVENTS (EVENT_ID, TITLE, DESCRIPTION, PLACE_ID, GROUP_ID, DEPARTMENT_ID,
            ROOM, ADDRESS, CITY, STATE, EVENT_URL, PHOTO_URL, TICKET_URL, FACEBOOK_URL, COST,
            HASHTAG, KEYWORDS, TAGS, ALLOWS_REVIEWS, SPONSORED, VENUE_PAGE_ONLY,
            EXCLUDE_FROM_TRENDING, VISIBILITY, FILTERS, CUSTOM_FIELDS, CREATED_AT)
        VALUES (:id,
            :title,
            :description,
            (SELECT PLACE_ID FROM PLACES
                WHERE NAME = :location),
            (SELECT GROUP_ID FROM GROUPS
                WHERE NAME = :group
                OR PAGE_NAME = :group),
            (SELECT DEPARTMENT_ID FROM DEPARTMENTS
                WHERE NAME = :department),
            :room,
            :address,
            :city,
            :state,
            :eventURL,
            :photoURL,
            :ticketURL,
            :facebookURL,
            :cost,
            :hashtag,
            :keywords,
            :tags,
            :allowsReviews,
            :sponsored,
            :venuePageOnly,
            :excludeFromTrending,
            :visibility,
            :filterData,
            :customFieldData,
            SYSDATE)
        """)
    void createEvent(@Bind("id") String id,
                     @BindBean Event event,
                     @Bind("filterData") String filterData,
                     @Bind("customFieldData") String customFieldData)

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

    /**
     * PATCH by ID
     */
    @SqlUpdate("""
        UPDATE EVENTS
        SET
            TITLE =                 :title,
            DESCRIPTION =           :description,
            PLACE_ID =              (SELECT PLACE_ID FROM PLACES
                                        WHERE NAME = :location),
            GROUP_ID =              (SELECT GROUP_ID FROM GROUPS
                                        WHERE NAME = :group
                                        OR PAGE_NAME = :group),
            DEPARTMENT_ID =         (SELECT DEPARTMENT_ID FROM DEPARTMENTS
                                        WHERE NAME = :department),
            ROOM =                  :room,
            ADDRESS =               :address,
            CITY =                  :city,
            STATE =                 :state,
            EVENT_URL =             :eventURL,
            PHOTO_URL =             :photoURL,
            TICKET_URL =            :ticketURL,
            FACEBOOK_URL =          :facebookURL,
            COST =                  :cost,
            HASHTAG =               :hashtag,
            KEYWORDS =              :keywords,
            TAGS =                  :tags,
            ALLOWS_REVIEWS =        :allowsReviews,
            SPONSORED =             :sponsored,
            VENUE_PAGE_ONLY =       :venuePageOnly,
            EXCLUDE_FROM_TRENDING = :excludeFromTrending,
            VISIBILITY =            :visibility,
            FILTERS =               :filterData,
            CUSTOM_FIELDS =         :customFieldData,
            UPDATED_AT =            SYSDATE
        WHERE EVENT_ID =            :id
    """)
    void updateEvent(@Bind("id") String id,
                     @BindBean Event event,
                     @Bind("filterData") String filterData,
                     @Bind("customFieldData") String customFieldData)

    @SqlUpdate("""
        UPDATE INSTANCES
        SET
            START_TIME = TO_DATE(:start_date, 'yyyy-mm-dd hh24:mi:ss'),
            END_TIME = TO_DATE(:end_date, 'yyyy-mm-dd hh24:mi:ss')
        WHERE CLIENT_INSTANCE_ID = :client_instance_id
        AND EVENT_ID = :event_id
    """)
    void updateInstance(@Bind("client_instance_id") String instanceID,
                        @Bind("event_id") String eventID,
                        @Bind("start_date") String start,
                        @Bind("end_date") String end)

    /**
     * DELETE by ID
     */
    @SqlUpdate("""
        UPDATE EVENTS
            SET DELETED_AT = SYSDATE
            WHERE EVENT_ID=:id
    """)
    void deleteEvent(@Bind("id") String eventID)
}
