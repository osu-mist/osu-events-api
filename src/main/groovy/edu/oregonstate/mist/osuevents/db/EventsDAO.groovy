package edu.oregonstate.mist.osuevents.db

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
    @SqlQuery("""
        SELECT
            EVENT_ID,
            TITLE,
            DESCRIPTION,
            LOCATION_ID,
            ROOM,
            ADDRESS,
            CITY,
            STATE,
            COUNTY,
            EVENT_URL,
            PHOTO_URL,
            FACEBOOK_URL,
            TICKET_URL,
            TICKET_COST,
            HASHTAG,
            KEYWORDS,
            TAGS,
            GROUP_ID,
            ALLOWS_REVIEWS,
            SPONSORED,
            VENUE_PAGE_ONLY,
            EXCLUDE_FROM_TRENDING,
            ALLOW_USER_ACTIVITY,
            ALLOW_USER_INTEREST,
            DEPARTMENT_ID,
            CONTACT_NAME,
            CONTACT_EMAIL,
            CONTACT_PHONE,
            EVENT_TYPE_ID,
            EVENT_TOPIC_ID,
            AUDIENCE_ID,
            OWNER
        FROM EVENTS_EVENTS
        WHERE (EVENT_ID = :eventID OR :eventID IS NULL)
        AND DELETED_AT IS NULL
    """)
    @Mapper(EventMapper)
    List<Event> getEvents(@Bind("eventID") String eventID)

    @SqlQuery("""
        SELECT
            START_TIME AT TIME ZONE 'UTC' AS START_TIME,
            END_TIME AT TIME ZONE 'UTC' AS END_TIME
        FROM EVENTS_INSTANCES
        WHERE EVENT_ID = :eventID
        ORDER BY START_TIME
    """)
    @Mapper(InstanceMapper)
    List<Instance> getInstances(@Bind("eventID") String eventID)

    @SqlUpdate("""
        INSERT INTO EVENTS_EVENTS (
            EVENT_ID,
            TITLE,
            DESCRIPTION,
            LOCATION_ID,
            ROOM,
            ADDRESS,
            CITY,
            STATE,
            COUNTY,
            EVENT_URL,
            PHOTO_URL,
            FACEBOOK_URL,
            TICKET_URL,
            TICKET_COST,
            HASHTAG,
            KEYWORDS,
            TAGS,
            GROUP_ID,
            ALLOWS_REVIEWS,
            SPONSORED,
            VENUE_PAGE_ONLY,
            EXCLUDE_FROM_TRENDING,
            ALLOW_USER_ACTIVITY,
            ALLOW_USER_INTEREST,
            DEPARTMENT_ID,
            CONTACT_NAME,
            CONTACT_EMAIL,
            CONTACT_PHONE,
            EVENT_TYPE_ID,
            EVENT_TOPIC_ID,
            AUDIENCE_ID,
            OWNER,
            CREATED_AT)
        VALUES (
            :eventID,
            :title,
            :description,
            :locationID,
            :room,
            :address,
            :city,
            :state,
            :county,
            :eventURL,
            :photoURL,
            :facebookURL,
            :ticketURL,
            :ticketCost,
            :hashtag,
            :keywords,
            :tags,
            :groupID,
            :allowsReviews,
            :sponsored,
            :venuePageOnly,
            :excludeFromTrending,
            :allowUserActivity,
            :allowUserInterest,
            :departmentID,
            :contactName,
            :contactEmail,
            :contactPhone,
            :eventTypeID,
            :eventTopicID,
            :audienceID,
            :owner,
            SYSDATE)
    """)
    void createEvent(@BindEvent Event event)

    @SqlUpdate("""
        INSERT INTO EVENTS_INSTANCES (
            EVENT_ID,
            START_TIME,
            END_TIME,
            CREATED_AT)
        VALUES (
            :eventID,
            :start,
            :end,
            SYSDATE)
    """)
    void createInstance(@BindBean Instance instance,
                        @Bind("eventID") String eventID)

    @SqlUpdate("""
        UPDATE EVENTS_EVENTS
        SET
            TITLE = :title,
            DESCRIPTION = :description,
            LOCATION_ID = :locationID,
            ROOM = :room,
            ADDRESS = :address,
            CITY = :city,
            STATE = :state,
            COUNTY = :county,
            EVENT_URL = :eventURL,
            PHOTO_URL = :photoURL,
            FACEBOOK_URL = :facebookURL,
            TICKET_URL = :ticketURL,
            TICKET_COST = :ticketCost,
            HASHTAG = :hashtag,
            KEYWORDS = :keywords,
            TAGS = :tags,
            GROUP_ID = :groupID,
            ALLOWS_REVIEWS = :allowsReviews,
            SPONSORED = :sponsored,
            VENUE_PAGE_ONLY = :venuePageOnly,
            EXCLUDE_FROM_TRENDING = :excludeFromTrending,
            ALLOW_USER_ACTIVITY = :allowUserActivity,
            ALLOW_USER_INTEREST = :allowUserInterest,
            DEPARTMENT_ID = :departmentID,
            CONTACT_NAME = :contactName,
            CONTACT_EMAIL = :contactEmail,
            CONTACT_PHONE = :contactPhone,
            EVENT_TYPE_ID = :eventTypeID,
            EVENT_TOPIC_ID = :eventTopicID,
            AUDIENCE_ID = :audienceID,
            UPDATED_AT = SYSDATE
        WHERE EVENT_ID = :eventID
    """)
    void updateEvent(@BindEvent Event event)

    @SqlUpdate("""
        UPDATE EVENTS_EVENTS
        SET
            DELETED_AT = SYSDATE
        WHERE EVENT_ID = :eventID
    """)
    void deleteEvent(@Bind("eventID") String eventID)

    @SqlUpdate("""
        DELETE FROM EVENTS_INSTANCES
        WHERE EVENT_ID = :eventID
    """)
    void deleteInstances(@Bind("eventID") String eventID)

    @SqlQuery("SELECT 1 FROM dual")
    Integer checkHealth()
}
