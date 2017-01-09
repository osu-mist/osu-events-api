package edu.oregonstate.mist.osuevents.db

import edu.oregonstate.mist.osuevents.core.Event
import edu.oregonstate.mist.osuevents.core.Instance
import edu.oregonstate.mist.osuevents.mapper.EventMapper
import edu.oregonstate.mist.osuevents.mapper.InstanceMapper
import org.skife.jdbi.v2.sqlobject.Bind
import org.skife.jdbi.v2.sqlobject.SqlQuery
import org.skife.jdbi.v2.sqlobject.customizers.Mapper

public interface EventsDAO extends Closeable {

    /**
     * Get by ID
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
            TO_CHAR(START_TIME, 'yyyy-mm-dd hh:mm:ss') AS START_TIME,
            TO_CHAR(END_TIME, 'yyyy-mm-dd hh:mm:ss') AS END_TIME
        FROM INSTANCES
        WHERE EVENT_ID=:id
        """)
    @Mapper(InstanceMapper)
    List<Instance> getInstances(@Bind("id") String id)

    /**
     * Get all events
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
}
