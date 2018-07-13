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
            OTHER_LOCATION_NAME,
            ROOM,
            ADDRESS,
            CITY,
            STATE,
            COUNTY_IDS,
            CAMPUS_ID,
            EVENT_URL,
            PHOTO_URL,
            TICKET_URL,
            TICKET_COST,
            HASHTAG,
            KEYWORDS,
            TAGS,
            ALLOWS_REVIEWS,
            ALLOW_USER_ACTIVITY,
            DEPARTMENT_IDS,
            CONTACT_NAME,
            CONTACT_EMAIL,
            CONTACT_PHONE,
            EVENT_TYPE_IDS,
            EVENT_TOPIC_IDS,
            AUDIENCE_IDS,
            VISIBILITY,
            OWNER
        FROM EVENTS_EVENTS
        WHERE (EVENT_ID = :eventID OR :eventID IS NULL)
	   AND (CREATED_AT >= SYSDATE - :changedInPastHours/24 OR :changedInPastHours IS NULL)
	   AND (UPDATED_AT >= SYSDATE - :changedInPastHours/24)
        AND DELETED_AT IS NULL
    """)
    @Mapper(EventMapper)
    List<Event> getEvents(@Bind("eventID") String,
    				@Bind("changedInPastHours") Integer changedInPastHours)

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
            OTHER_LOCATION_NAME,
            ROOM,
            ADDRESS,
            CITY,
            STATE,
            COUNTY_IDS,
            CAMPUS_ID,
            EVENT_URL,
            PHOTO_URL,
            TICKET_URL,
            TICKET_COST,
            HASHTAG,
            KEYWORDS,
            TAGS,
            ALLOWS_REVIEWS,
            ALLOW_USER_ACTIVITY,
            DEPARTMENT_IDS,
            CONTACT_NAME,
            CONTACT_EMAIL,
            CONTACT_PHONE,
            EVENT_TYPE_IDS,
            EVENT_TOPIC_IDS,
            AUDIENCE_IDS,
            VISIBILITY,
            OWNER,
            CREATED_AT)
        VALUES (
            :eventID,
            :title,
            :description,
            :locationID,
            :otherLocationName,
            :room,
            :address,
            :city,
            :state,
            :countyIDs,
            :campusID,
            :eventURL,
            :photoURL,
            :ticketURL,
            :ticketCost,
            :hashtag,
            :keywords,
            :tags,
            :allowsReviews,
            :allowUserActivity,
            :departmentIDs,
            :contactName,
            :contactEmail,
            :contactPhone,
            :eventTypeIDs,
            :eventTopicIDs,
            :audienceIDs,
            :visibility,
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
            OTHER_LOCATION_NAME = :otherLocationName,
            ROOM = :room,
            ADDRESS = :address,
            CITY = :city,
            STATE = :state,
            COUNTY_IDS = :countyIDs,
            CAMPUS_ID = :campusID,
            EVENT_URL = :eventURL,
            PHOTO_URL = :photoURL,
            TICKET_URL = :ticketURL,
            TICKET_COST = :ticketCost,
            HASHTAG = :hashtag,
            KEYWORDS = :keywords,
            TAGS = :tags,
            ALLOWS_REVIEWS = :allowsReviews,
            ALLOW_USER_ACTIVITY = :allowUserActivity,
            DEPARTMENT_IDS = :departmentIDs,
            CONTACT_NAME = :contactName,
            CONTACT_EMAIL = :contactEmail,
            CONTACT_PHONE = :contactPhone,
            EVENT_TYPE_IDS = :eventTypeIDs,
            EVENT_TOPIC_IDS = :eventTopicIDs,
            AUDIENCE_IDS = :audienceIDs,
            VISIBILITY = :visibility,
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
