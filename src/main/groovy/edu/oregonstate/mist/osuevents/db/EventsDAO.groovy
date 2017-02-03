package edu.oregonstate.mist.osuevents.db

import edu.oregonstate.mist.api.jsonapi.ResourceObject
import edu.oregonstate.mist.osuevents.core.CacheObject
import edu.oregonstate.mist.osuevents.core.Event
import edu.oregonstate.mist.osuevents.core.Instance
import edu.oregonstate.mist.osuevents.mapper.CacheObjectMapper
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
        SELECT
            apismgr.EVENTS_EVENTS.EVENT_ID,
            apismgr.EVENTS_EVENTS.TITLE,
            apismgr.EVENTS_EVENTS.DESCRIPTION,
            apismgr.EVENTS_PLACES.NAME AS PLACE_NAME,
            apismgr.EVENTS_EVENTS.ROOM,
            apismgr.EVENTS_EVENTS.ADDRESS,
            apismgr.EVENTS_EVENTS.CITY,
            apismgr.EVENTS_EVENTS.STATE,
            apismgr.EVENTS_EVENTS.EVENT_URL,
            apismgr.EVENTS_EVENTS.PHOTO_URL,
            apismgr.EVENTS_EVENTS.TICKET_URL,
            apismgr.EVENTS_EVENTS.FACEBOOK_URL,
            apismgr.EVENTS_EVENTS.COST,
            apismgr.EVENTS_EVENTS.HASHTAG,
            apismgr.EVENTS_EVENTS.KEYWORDS,
            apismgr.EVENTS_EVENTS.TAGS,
            apismgr.EVENTS_GROUPS.NAME AS GROUP_NAME,
            apismgr.EVENTS_DEPARTMENTS.NAME AS DEPARTMENT_NAME,
            apismgr.EVENTS_EVENTS.ALLOWS_REVIEWS,
            apismgr.EVENTS_EVENTS.SPONSORED,
            apismgr.EVENTS_EVENTS.VENUE_PAGE_ONLY,
            apismgr.EVENTS_EVENTS.EXCLUDE_FROM_TRENDING,
            apismgr.EVENTS_EVENTS.VISIBILITY,
            apismgr.EVENTS_EVENTS.FILTERS,
            apismgr.EVENTS_EVENTS.CUSTOM_FIELDS
        FROM apismgr.EVENTS_EVENTS
        LEFT JOIN apismgr.EVENTS_PLACES
          ON apismgr.EVENTS_EVENTS.PLACE_ID = apismgr.EVENTS_PLACES.PLACE_ID
        LEFT JOIN apismgr.EVENTS_GROUPS
          ON apismgr.EVENTS_EVENTS.GROUP_ID = apismgr.EVENTS_GROUPS.GROUP_ID
        LEFT JOIN apismgr.EVENTS_DEPARTMENTS
          ON apismgr.EVENTS_EVENTS.DEPARTMENT_ID = apismgr.EVENTS_DEPARTMENTS.DEPARTMENT_ID
        WHERE EVENT_ID=:id
        AND apismgr.EVENTS_EVENTS.DELETED_AT IS NULL
        """)
    @Mapper(EventMapper)
    ResourceObject getById(@Bind("id") String id)

    @SqlQuery("""
        SELECT
            CLIENT_INSTANCE_ID,
            TO_CHAR(START_TIME, 'yyyy-mm-dd hh24:mi:ss') AS START_TIME,
            TO_CHAR(END_TIME, 'yyyy-mm-dd hh24:mi:ss') AS END_TIME
        FROM apismgr.EVENTS_INSTANCES
        WHERE EVENT_ID=:id
        AND DELETED_AT IS NULL
        """)
    @Mapper(InstanceMapper)
    List<Instance> getInstances(@Bind("id") String id)

    @SqlQuery("""
        SELECT
            CLIENT_INSTANCE_ID,
            TO_CHAR(START_TIME, 'yyyy-mm-dd hh24:mi:ss') AS START_TIME,
            TO_CHAR(END_TIME, 'yyyy-mm-dd hh24:mi:ss') AS END_TIME
        FROM apismgr.EVENTS_INSTANCES
        WHERE EVENT_ID=:event_id
        AND CLIENT_INSTANCE_ID=:instance_id
        AND DELETED_AT IS NULL
        """)
    @Mapper(InstanceMapper)
    Instance getInstance(@Bind("event_id") String eventID,
                         @Bind("instance_id") String instanceID)

    /**
     * GET all events
     */
    @SqlQuery("""
        SELECT
            apismgr.EVENTS_EVENTS.EVENT_ID,
            apismgr.EVENTS_EVENTS.TITLE,
            apismgr.EVENTS_EVENTS.DESCRIPTION,
            apismgr.EVENTS_PLACES.NAME as PLACE_NAME,
            apismgr.EVENTS_EVENTS.ROOM,
            apismgr.EVENTS_EVENTS.ADDRESS,
            apismgr.EVENTS_EVENTS.CITY,
            apismgr.EVENTS_EVENTS.STATE,
            apismgr.EVENTS_EVENTS.EVENT_URL,
            apismgr.EVENTS_EVENTS.PHOTO_URL,
            apismgr.EVENTS_EVENTS.TICKET_URL,
            apismgr.EVENTS_EVENTS.FACEBOOK_URL,
            apismgr.EVENTS_EVENTS.COST,
            apismgr.EVENTS_EVENTS.HASHTAG,
            apismgr.EVENTS_EVENTS.KEYWORDS,
            apismgr.EVENTS_EVENTS.TAGS,
            apismgr.EVENTS_GROUPS.NAME as GROUP_NAME,
            apismgr.EVENTS_DEPARTMENTS.NAME as DEPARTMENT_NAME,
            apismgr.EVENTS_EVENTS.ALLOWS_REVIEWS,
            apismgr.EVENTS_EVENTS.SPONSORED,
            apismgr.EVENTS_EVENTS.VENUE_PAGE_ONLY,
            apismgr.EVENTS_EVENTS.EXCLUDE_FROM_TRENDING,
            apismgr.EVENTS_EVENTS.VISIBILITY,
            apismgr.EVENTS_EVENTS.FILTERS,
            apismgr.EVENTS_EVENTS.CUSTOM_FIELDS
        FROM apismgr.EVENTS_EVENTS
        LEFT JOIN apismgr.EVENTS_PLACES
          ON apismgr.EVENTS_EVENTS.PLACE_ID = apismgr.EVENTS_PLACES.PLACE_ID
        LEFT JOIN apismgr.EVENTS_GROUPS
          ON apismgr.EVENTS_EVENTS.GROUP_ID = apismgr.EVENTS_GROUPS.GROUP_ID
        LEFT JOIN apismgr.EVENTS_DEPARTMENTS
          ON apismgr.EVENTS_EVENTS.DEPARTMENT_ID = apismgr.EVENTS_DEPARTMENTS.DEPARTMENT_ID
        WHERE apismgr.EVENTS_EVENTS.DELETED_AT IS NULL
        """)
    @Mapper(EventMapper)
    List<ResourceObject> getEvents()

    /**
     * POST a new event
     */
    @SqlUpdate("""
        INSERT INTO apismgr.EVENTS_EVENTS  (EVENT_ID, TITLE, DESCRIPTION, PLACE_ID,
            GROUP_ID, DEPARTMENT_ID, ROOM, ADDRESS, CITY, STATE, EVENT_URL,
            PHOTO_URL, TICKET_URL, FACEBOOK_URL, COST, HASHTAG, KEYWORDS, TAGS,
            ALLOWS_REVIEWS, SPONSORED, VENUE_PAGE_ONLY, EXCLUDE_FROM_TRENDING,
            VISIBILITY, FILTERS, CUSTOM_FIELDS, CREATED_AT)
        VALUES (:id,
            :title,
            :description,
            (SELECT PLACE_ID FROM apismgr.EVENTS_PLACES
                WHERE NAME = :location
                AND DELETED_AT IS NULL),
            (SELECT GROUP_ID FROM apismgr.EVENTS_GROUPS
                WHERE (NAME = :group
                OR PAGE_NAME = :group)
                AND DELETED_AT IS NULL),
            (SELECT DEPARTMENT_ID FROM apismgr.EVENTS_DEPARTMENTS
                WHERE (NAME = :department
                OR PAGE_NAME = :department)
                AND DELETED_AT IS NULL),
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
        INSERT INTO apismgr.EVENTS_INSTANCES (INSTANCE_ID,
                               CLIENT_INSTANCE_ID,
                               EVENT_ID,
                               START_TIME,
                               END_TIME,
                               CREATED_AT)
        VALUES (apismgr.EVENTS_INSTANCE_SEQ.NEXTVAL,
            :client_instance_id,
            :event_id,
            TO_DATE(:start_date, 'yyyy-mm-dd hh24:mi:ss'),
            TO_DATE(:end_date, 'yyyy-mm-dd hh24:mi:ss'),
            SYSDATE)
        """)
    void createInstance(@Bind("client_instance_id") String instanceID,
                        @Bind("event_id") String eventID,
                        @Bind("start_date") String start,
                        @Bind("end_date") String end)

    /**
     * PATCH by ID
     */
    @SqlUpdate("""
        UPDATE apismgr.EVENTS_EVENTS
        SET
            TITLE =                 :title,
            DESCRIPTION =           :description,
            PLACE_ID =              (SELECT PLACE_ID FROM apismgr.EVENTS_PLACES
                                        WHERE NAME = :location
                                        AND DELETED_AT IS NULL),
            GROUP_ID =              (SELECT GROUP_ID FROM apismgr.EVENTS_GROUPS
                                        WHERE (NAME = :group
                                        OR PAGE_NAME = :group)
                                        AND DELETED_AT IS NULL),
            DEPARTMENT_ID =         (SELECT DEPARTMENT_ID FROM apismgr.EVENTS_DEPARTMENTS
                                        WHERE (NAME = :department
                                        OR PAGE_NAME = :department)
                                        AND DELETED_AT IS NULL),
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
        UPDATE apismgr.EVENTS_INSTANCES
        SET
            START_TIME = TO_DATE(:start_date, 'yyyy-mm-dd hh24:mi:ss'),
            END_TIME = TO_DATE(:end_date, 'yyyy-mm-dd hh24:mi:ss'),
            UPDATED_AT = SYSDATE
        WHERE CLIENT_INSTANCE_ID=:client_instance_id
        AND EVENT_ID=:event_id
    """)
    void updateInstance(@Bind("client_instance_id") String instanceID,
                        @Bind("event_id") String eventID,
                        @Bind("start_date") String start,
                        @Bind("end_date") String end)

    /**
     * DELETE by ID
     */
    @SqlUpdate("""
        UPDATE apismgr.EVENTS_EVENTS
            SET DELETED_AT = SYSDATE
            WHERE EVENT_ID = :id
    """)
    void deleteEvent(@Bind("id") String eventID)

    @SqlUpdate("""
        UPDATE apismgr.EVENTS_INSTANCES
            SET DELETED_AT = SYSDATE
            WHERE EVENT_ID=:event_id
            AND CLIENT_INSTANCE_ID=:instance_id
    """)
    void deleteInstance(@Bind("event_id") String eventID,
                        @Bind("instance_id") String instanceID)

    @SqlQuery("SELECT CUSTOM_FIELD_ID AS ID, NAME FROM apismgr.EVENTS_CUSTOM_FIELDS")
    @Mapper(CacheObjectMapper)
    List<CacheObject> getCustomFields()

    @SqlUpdate("""
        INSERT INTO apismgr.EVENTS_CUSTOM_FIELDS (CUSTOM_FIELD_ID, NAME, CREATED_AT)
            VALUES (:id, :name, SYSDATE)
    """)
    void createCustomField(@BindBean CacheObject customField)

    @SqlUpdate("""
        UPDATE apismgr.EVENTS_CUSTOM_FIELDS
          SET
            NAME = :name,
            UPDATED_AT = SYSDATE
          WHERE CUSTOM_FIELD_ID = :id
    """)
    void updateCustomField(@BindBean CacheObject customField)

    @SqlUpdate("""
        DELETE FROM apismgr.EVENTS_CUSTOM_FIELDS
            WHERE CUSTOM_FIELD_ID = :id
    """)
    void deleteCustomField(@Bind("id") def customFieldID)

    @SqlQuery("""
        SELECT CUSTOM_FIELD_ID FROM apismgr.EVENTS_CUSTOM_FIELDS
            WHERE NAME = :field
            AND DELETED_AT IS NULL
    """)
    String checkCustomField(@Bind("field") String field)

    @SqlQuery("SELECT DEPARTMENT_ID AS ID, NAME FROM apismgr.EVENTS_DEPARTMENTS ")
    @Mapper(CacheObjectMapper)
    List<CacheObject> getDepartments()

    @SqlUpdate("""
        INSERT INTO apismgr.EVENTS_DEPARTMENTS  (DEPARTMENT_ID, NAME, CREATED_AT)
            VALUES (:id, :name, SYSDATE)
    """)
    void createDepartment(@BindBean CacheObject group)

    @SqlUpdate("""
        UPDATE apismgr.EVENTS_DEPARTMENTS
          SET
            NAME = :name,
            UPDATED_AT = SYSDATE
          WHERE DEPARTMENT_ID = :id
    """)
    void updateDepartment(@BindBean CacheObject group)

    @SqlUpdate("""
        DELETE FROM apismgr.EVENTS_DEPARTMENTS
            WHERE DEPARTMENT_ID = :id
    """)
    void deleteDepartment(@Bind("id") def groupID)

    @SqlQuery("""
        SELECT DEPARTMENT_ID FROM apismgr.EVENTS_DEPARTMENTS
            WHERE NAME = :department
            AND DELETED_AT IS NULL
    """)
    String checkDepartment(@Bind("department") String department)

    @SqlQuery("SELECT FILTER_ID AS ID, NAME FROM apismgr.EVENTS_FILTERS")
    @Mapper(CacheObjectMapper)
    List<CacheObject> getFilters()

    @SqlUpdate("""
        INSERT INTO apismgr.EVENTS_FILTERS (FILTER_ID, NAME, CREATED_AT)
            VALUES (:id, :name, SYSDATE)
    """)
    void createFilter(@BindBean CacheObject filter)

    @SqlUpdate("""
        UPDATE apismgr.EVENTS_FILTERS
          SET
            NAME = :name,
            UPDATED_AT = SYSDATE
          WHERE FILTER_ID = :id
    """)
    void updateFilter(@BindBean CacheObject filter)

    @SqlUpdate("""
        DELETE FROM apismgr.EVENTS_FILTERS
            WHERE FILTER_ID = :id
    """)
    void deleteFilter(@Bind("id") def filterID)

    @SqlQuery ("""
        SELECT FILTER_ID FROM apismgr.EVENTS_FILTERS
          WHERE NAME = :filter
          AND DELETED_AT IS NULL
    """)
    String checkFilter(@Bind("filter") String filter)

    @SqlQuery("""
        SELECT ITEM_ID AS ID, NAME FROM apismgr.EVENTS_FILTER_ITEMS
            WHERE FILTER_ID = :filter_id
    """)
    @Mapper(CacheObjectMapper)
    List<CacheObject> getFilterItems(@Bind("filter_id") def filterID)

    @SqlUpdate("""
        INSERT INTO apismgr.EVENTS_FILTER_ITEMS (ITEM_ID, FILTER_ID, NAME, CREATED_AT)
            VALUES (:id, :filter_id, :name, SYSDATE)
    """)
    void createFilterItem(@BindBean CacheObject filterItem,
                          @Bind("filter_id") def filterID)

    @SqlUpdate("""
        UPDATE apismgr.EVENTS_FILTER_ITEMS
          SET
            NAME = :name,
            UPDATED_AT = SYSDATE
          WHERE ITEM_ID = :id
          AND FILTER_ID = :filter_id
    """)
    void updateFilterItem(@BindBean CacheObject filterItem,
                          @Bind("filter_id") def filterID)

    @SqlUpdate("""
        DELETE FROM apismgr.EVENTS_FILTER_ITEMS
            WHERE ITEM_ID = :id
            AND FILTER_ID = :filter_id
    """)
    void deleteFilterItem(@Bind("id") def filterItemID,
                          @Bind("filter_id") def filterID)

    @SqlQuery ("""
        SELECT apismgr.EVENTS_FILTER_ITEMS.ITEM_ID FROM apismgr.EVENTS_FILTER_ITEMS
          LEFT JOIN apismgr.EVENTS_FILTERS
            ON apismgr.EVENTS_FILTER_ITEMS.FILTER_ID = apismgr.EVENTS_FILTERS.FILTER_ID
          WHERE apismgr.EVENTS_FILTER_ITEMS.NAME = :item
          AND apismgr.EVENTS_FILTERS.FILTER_ID = :filterID
          AND apismgr.EVENTS_FILTER_ITEMS.DELETED_AT IS NULL
    """)
    String checkFilterItem(@Bind("item") String item,
                           @Bind("filterID") String filterID)

    @SqlQuery("SELECT GROUP_ID AS ID, NAME FROM apismgr.EVENTS_GROUPS")
    @Mapper(CacheObjectMapper)
    List<CacheObject> getGroups()

    @SqlUpdate("""
        INSERT INTO apismgr.EVENTS_GROUPS (GROUP_ID, NAME, CREATED_AT)
            VALUES (:id, :name, SYSDATE)
    """)
    void createGroup(@BindBean CacheObject group)

    @SqlUpdate("""
        UPDATE apismgr.EVENTS_GROUPS
          SET
            NAME = :name,
            UPDATED_AT = SYSDATE
          WHERE GROUP_ID = :id
    """)
    void updateGroup(@BindBean CacheObject group)

    @SqlUpdate("""
        DELETE FROM apismgr.EVENTS_GROUPS
            WHERE GROUP_ID = :id
    """)
    void deleteGroup(@Bind("id") def groupID)

    @SqlQuery("""
        SELECT GROUP_ID FROM apismgr.EVENTS_GROUPS
            WHERE NAME = :group
            AND DELETED_AT IS NULL
    """)
    String checkGroup(@Bind("group") String group)

    @SqlQuery("SELECT PLACE_ID AS ID, NAME FROM apismgr.EVENTS_PLACES")
    @Mapper(CacheObjectMapper)
    List<CacheObject> getPlaces()

    @SqlUpdate("""
        INSERT INTO apismgr.EVENTS_PLACES (PLACE_ID, NAME, CREATED_AT)
            VALUES (:id, :name, SYSDATE)
    """)
    void createPlace(@BindBean CacheObject place)

    @SqlUpdate("""
        UPDATE apismgr.EVENTS_PLACES
          SET
            NAME = :name,
            UPDATED_AT = SYSDATE
          WHERE PLACE_ID = :id
    """)
    void updatePlace(@BindBean CacheObject place)

    @SqlUpdate("""
        DELETE FROM apismgr.EVENTS_PLACES
            WHERE PLACE_ID = :id
    """)
    void deletePlace(@Bind("id") def placeID)

    @SqlQuery("""
        SELECT PLACE_ID FROM apismgr.EVENTS_PLACES
            WHERE NAME = :location
            AND DELETED_AT IS NULL
    """)
    String checkPlace(@Bind("location") String location)

    @SqlQuery("SELECT 1 FROM dual")
    Integer checkHealth()
}
