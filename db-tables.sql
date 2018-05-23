CREATE TABLE apismgr.EVENTS_EVENTS 
(
  EVENT_ID              VARCHAR2(36) NOT NULL,
  TITLE                 VARCHAR2(256),
  DESCRIPTION           CLOB,
  LOCATION_ID           NUMBER,
  ROOM                  VARCHAR2(256),
  ADDRESS               VARCHAR2(256),
  CITY                  VARCHAR2(256),
  STATE                 VARCHAR2(256),
  COUNTY                VARCHAR2(256),
  EVENT_URL             VARCHAR2(256),
  PHOTO_URL             VARCHAR2(256),
  FACEBOOK_URL          VARCHAR2(256),
  TICKET_URL            VARCHAR2(256),
  TICKET_COST           VARCHAR2(768),
  HASHTAG               VARCHAR2(256),
  KEYWORDS              CLOB,
  TAGS                  CLOB,
  GROUP_ID              NUMBER,
  ALLOWS_REVIEWS        VARCHAR2(1),
  SPONSORED             VARCHAR2(1),
  VENUE_PAGE_ONLY       VARCHAR2(1),
  EXCLUDE_FROM_TRENDING VARCHAR2(1),
  ALLOW_USER_ACTIVITY   VARCHAR2(1),
  ALLOW_USER_INTEREST   VARCHAR2(1),
  DEPARTMENT_ID         NUMBER,
  CONTACT_NAME          VARCHAR2(256),
  CONTACT_EMAIL         VARCHAR2(256),
  CONTACT_PHONE         VARCHAR2(256),
  EVENT_TYPE_ID         NUMBER,
  EVENT_TOPIC_ID        NUMBER,
  AUDIENCE_ID           NUMBER,
  ORGANIZATION_ID       NUMBER,
  CREATED_AT            DATE NOT NULL,
  UPDATED_AT            DATE,
  DELETED_AT            DATE,
  CONSTRAINT PK_EVENTS_EVENTS PRIMARY KEY 
  (
    EVENT_ID 
  )
  USING INDEX TABLESPACE INDX)
    TABLESPACE DATA
/

CREATE PUBLIC SYNONYM EVENTS_EVENTS FOR apismgr.EVENTS_EVENTS;

COMMENT ON TABLE apismgr.EVENTS_EVENTS  IS 'Contains event data with EVENT_ID as the unique identifier.';
COMMENT ON COLUMN apismgr.EVENTS_EVENTS.EVENT_ID IS 'Primary key. 128 bit UUID.';
COMMENT ON COLUMN apismgr.EVENTS_EVENTS.TITLE IS 'Title of event.';
COMMENT ON COLUMN apismgr.EVENTS_EVENTS.DESCRIPTION IS 'Description for an event.';
COMMENT ON COLUMN apismgr.EVENTS_EVENTS.LOCATION_ID IS 'Foreign key for LOCATIONS table.';
COMMENT ON COLUMN apismgr.EVENTS_EVENTS.ROOM IS 'Specific location of event within a place.';
COMMENT ON COLUMN apismgr.EVENTS_EVENTS.ADDRESS IS 'Address of event'.;
COMMENT ON COLUMN apismgr.EVENTS_EVENTS.CITY IS 'City of event.';
COMMENT ON COLUMN apismgr.EVENTS_EVENTS.STATE IS 'State of event within the United States.';
COMMENT ON COLUMN apismgr.EVENTS_EVENTS.COUNTY IS 'County of event.';
COMMENT ON COLUMN apismgr.EVENTS_EVENTS.EVENT_URL IS 'External URL of website related event.';
COMMENT ON COLUMN apismgr.EVENTS_EVENTS.PHOTO_URL IS 'Direct URL of image to be used in event page.';
COMMENT ON COLUMN apismgr.EVENTS_EVENTS.FACEBOOK_URL IS 'External URL of Facebook event.';
COMMENT ON COLUMN apismgr.EVENTS_EVENTS.TICKET_URL IS 'External URL of website related to event tickets';
COMMENT ON COLUMN apismgr.EVENTS_EVENTS.TICKET_COST IS 'String explaining event cost information';
COMMENT ON COLUMN apismgr.EVENTS_EVENTS.HASHTAG IS 'Single hashtag not preceded by #';
COMMENT ON COLUMN apismgr.EVENTS_EVENTS.KEYWORDS IS 'JSON array of event keywords.';
COMMENT ON COLUMN apismgr.EVENTS_EVENTS.TAGS IS 'JSON array of event tags.';
COMMENT ON COLUMN apismgr.EVENTS_EVENTS.GROUP_ID IS 'Foreign key for GROUPS table.';
COMMENT ON COLUMN apismgr.EVENTS_EVENTS.ALLOWS_REVIEWS IS '1 or 0.';
COMMENT ON COLUMN apismgr.EVENTS_EVENTS.SPONSORED IS '1 or 0.';
COMMENT ON COLUMN apismgr.EVENTS_EVENTS.VENUE_PAGE_ONLY IS '1 or 0.';
COMMENT ON COLUMN apismgr.EVENTS_EVENTS.EXCLUDE_FROM_TRENDING IS '1 or 0.';
COMMENT ON COLUMN apismgr.EVENTS_EVENTS.ALLOW_USER_ACTIVITY IS '1 or 0.';
COMMENT ON COLUMN apismgr.EVENTS_EVENTS.ALLOW_USER_INTEREST IS '1 or 0.';
COMMENT ON COLUMN apismgr.EVENTS_EVENTS.DEPARTMENT_ID IS 'Foreign key for DEPARTMENTS table.';
COMMENT ON COLUMN apismgr.EVENTS_EVENTS.CONTACT_NAME IS 'Contact name of event organizer.';
COMMENT ON COLUMN apismgr.EVENTS_EVENTS.CONTACT_EMAIL IS 'Email of event organizer.';
COMMENT ON COLUMN apismgr.EVENTS_EVENTS.CONTACT_PHONE IS 'Phone number of event organizer.';
COMMENT ON COLUMN apismgr.EVENTS_EVENTS.EVENT_TYPE_ID IS 'Event type ID.';
COMMENT ON COLUMN apismgr.EVENTS_EVENTS.EVENT_TOPIC_ID IS 'Event topic ID.';
COMMENT ON COLUMN apismgr.EVENTS_EVENTS.AUDIENCE_ID IS 'Event audience ID.';
COMMENT ON COLUMN apismgr.EVENTS_EVENTS.ORGANIZATION_ID IS 'Event organization ID.';
COMMENT ON COLUMN apismgr.EVENTS_EVENTS.CREATED_AT IS 'Stores SYSDATE when event was created.';
COMMENT ON COLUMN apismgr.EVENTS_EVENTS.UPDATED_AT IS 'Stores SYSDATE when event was updated.';
COMMENT ON COLUMN apismgr.EVENTS_EVENTS.DELETED_AT IS 'Stores SYSDATE when event was deleted.';

CREATE TABLE apismgr.EVENTS_INSTANCES
(
  EVENT_ID           VARCHAR2(36) NOT NULL,
  START_TIME         TIMESTAMP WITH TIME ZONE,
  END_TIME           TIMESTAMP WITH TIME ZONE,
  CREATED_AT         DATE NOT NULL
)
/

CREATE PUBLIC SYNONYM EVENTS_INSTANCES FOR apismgr.EVENTS_INSTANCES;

COMMENT ON TABLE apismgr.EVENTS_INSTANCES IS 'Contains instances for events. One event can have multiple instances.';
COMMENT ON COLUMN apismgr.INSTANCES.EVENT_ID IS 'Foreign key for EVENTS table.';
COMMENT ON COLUMN apismgr.INSTANCES.START_TIME IS 'Start date/time of instance.';
COMMENT ON COLUMN apismgr.INSTANCES.END_TIME IS 'End date/time of instance.';
COMMENT ON COLUMN apismgr.INSTANCES.CREATED_AT IS 'Stores SYSDATE when instance was created.';

CREATE TABLE apismgr.EVENTS_GROUPS 
(
  GROUP_ID   VARCHAR2(36) NOT NULL,
  NAME       VARCHAR2(256),
  PAGE_NAME  VARCHAR2(256),
  CREATED_AT DATE NOT NULL,
  UPDATED_AT DATE,
  DELETED_AT DATE,
  CONSTRAINT PK_EVENTS_GROUPS PRIMARY KEY 
    (
      GROUP_ID 
    )
  USING INDEX TABLESPACE INDX)
    TABLESPACE DATA
/

CREATE PUBLIC SYNONYM EVENTS_GROUPS FOR apismgr.EVENTS_GROUPS;

COMMENT ON TABLE apismgr.EVENTS_GROUPS IS 'Contains groups used in vendor calendar system.';
COMMENT ON COLUMN apismgr.EVENTS_GROUPS.GROUP_ID IS 'Primary key.';
COMMENT ON COLUMN apismgr.EVENTS_GROUPS.NAME IS 'Plain name of group.';
COMMENT ON COLUMN apismgr.EVENTS_GROUPS.PAGE_NAME IS 'URI name of group used in vendor calendar system.';
COMMENT ON COLUMN apismgr.EVENTS_GROUPS.CREATED_AT IS 'Stores SYSDATE when group was created.';
COMMENT ON COLUMN apismgr.EVENTS_GROUPS.UPDATED_AT IS 'Stores SYSDATE when group was updated.';
COMMENT ON COLUMN apismgr.EVENTS_GROUPS.DELETED_AT IS 'Stores SYSDATE when group was deleted.';

CREATE TABLE apismgr.EVENTS_ORGANIZATIONS
(
  ORGANIZATION_ID   VARCHAR2(36) NOT NULL,
  NAME       VARCHAR2(256),
  CREATED_AT DATE NOT NULL,
  UPDATED_AT DATE,
  DELETED_AT DATE,
  CONSTRAINT PK_EVENTS_GROUPS PRIMARY KEY
    (
      ORGANIZATION_ID
    )
  USING INDEX TABLESPACE INDX)
    TABLESPACE DATA
/

CREATE PUBLIC SYNONYM EVENTS_ORGANIZATIONS FOR apismgr.EVENTS_ORGANIZATIONS;

COMMENT ON TABLE apismgr.EVENTS_ORGANIZATIONS IS 'Contains groups used in vendor calendar system.';
COMMENT ON COLUMN apismgr.EVENTS_ORGANIZATIONS.ORGANIZATION_ID IS 'Primary key.';
COMMENT ON COLUMN apismgr.EVENTS_ORGANIZATIONS.NAME IS 'Plain name of organization.';
COMMENT ON COLUMN apismgr.EVENTS_ORGANIZATIONS.CREATED_AT IS 'Stores SYSDATE when organization was created.';
COMMENT ON COLUMN apismgr.EVENTS_ORGANIZATIONS.UPDATED_AT IS 'Stores SYSDATE when organization was updated.';
COMMENT ON COLUMN apismgr.EVENTS_ORGANIZATIONS.DELETED_AT IS 'Stores SYSDATE when organization was deleted.';

CREATE TABLE apismgr.EVENTS_LOCATIONS
(
  LOCATION_ID VARCHAR2(36 CHAR) NOT NULL,
  NAME        VARCHAR2(256) NOT NULL,
  CREATED_AT  DATE NOT NULL,
  UPDATED_AT  DATE,
  DELETED_AT  DATE,
  CONSTRAINT PK_EVENTS_PLACES PRIMARY KEY 
    (
      LOCATION_ID
    )
  USING INDEX TABLESPACE INDX)
    TABLESPACE DATA
/

CREATE PUBLIC SYNONYM EVENTS_LOCATIONS FOR apismgr.EVENTS_LOCATIONS;

COMMENT ON TABLE apismgr.EVENTS_LOCATIONS IS 'Stores locations pulled from vendor calendar system.';
COMMENT ON COLUMN apismgr.EVENTS_LOCATIONS.LOCATION_ID IS 'Primary key.';
COMMENT ON COLUMN apismgr.EVENTS_LOCATIONS.NAME IS 'Name of locations/building.';
COMMENT ON COLUMN apismgr.EVENTS_LOCATIONS.CREATED_AT IS 'Stores SYSDATE when location was created.';
COMMENT ON COLUMN apismgr.EVENTS_LOCATIONS.UPDATED_AT IS 'Stores SYSDATE when location was updated.';
COMMENT ON COLUMN apismgr.EVENTS_LOCATIONS.DELETED_AT IS 'Stores SYSDATE when location was deleted.';

CREATE TABLE apismgr.EVENTS_DEPARTMENTS 
(
  DEPARTMENT_ID VARCHAR2(36) NOT NULL,
  NAME          VARCHAR2(256),
  PAGE_NAME     VARCHAR2(256),
  CREATED_AT    DATE NOT NULL,
  UPDATED_AT    DATE,
  DELETED_AT    DATE,
  CONSTRAINT PK_EVENTS_DEPARTMENTS PRIMARY KEY 
    (
      DEPARTMENT_ID 
    )
  USING INDEX TABLESPACE INDX)
    TABLESPACE DATA
/

CREATE PUBLIC SYNONYM EVENTS_DEPARTMENTS FOR apismgr.EVENTS_DEPARTMENTS;

COMMENT ON TABLE apismgr.EVENTS_DEPARTMENTS IS 'Contains departments used in vendor calendar system.';
COMMENT ON COLUMN apismgr.EVENTS_DEPARTMENTS.DEPARTMENT_ID IS 'Primary key.';
COMMENT ON COLUMN apismgr.EVENTS_DEPARTMENTS.NAME IS 'Plain name of department.';
COMMENT ON COLUMN apismgr.EVENTS_DEPARTMENTS.PAGE_NAME IS 'URI name of department used in vendor calendar system.';
COMMENT ON COLUMN apismgr.EVENTS_DEPARTMENTS.CREATED_AT IS 'Stores SYSDATE when department was created.';
COMMENT ON COLUMN apismgr.EVENTS_DEPARTMENTS.UPDATED_AT IS 'Stores SYSDATE when department was updated.';
COMMENT ON COLUMN apismgr.EVENTS_DEPARTMENTS.DELETED_AT IS 'Stores SYSDATE when department was deleted.';

CREATE TABLE apismgr.EVENTS_EVENT_TYPES
(
  EVENT_TYPE_ID   VARCHAR2(36) NOT NULL,
  NAME            VARCHAR2(256),
  CREATED_AT      DATE NOT NULL,
  UPDATED_AT      DATE,
  DELETED_AT      DATE,
  CONSTRAINT PK_EVENTS_GROUPS PRIMARY KEY
    (
      EVENT_TYPE_ID
    )
  USING INDEX TABLESPACE INDX)
    TABLESPACE DATA
/

CREATE PUBLIC SYNONYM EVENTS_EVENT_TYPES FOR apismgr.EVENTS_EVENT_TYPES;

COMMENT ON TABLE apismgr.EVENTS_EVENT_TYPES IS 'Contains event types used in vendor calendar system.';
COMMENT ON COLUMN apismgr.EVENTS_EVENT_TYPES.EVENT_TYPE_ID IS 'Primary key.';
COMMENT ON COLUMN apismgr.EVENTS_EVENT_TYPES.NAME IS 'Plain name of event type.';
COMMENT ON COLUMN apismgr.EVENTS_EVENT_TYPES.CREATED_AT IS 'Stores SYSDATE when event type was created.';
COMMENT ON COLUMN apismgr.EVENTS_EVENT_TYPES.UPDATED_AT IS 'Stores SYSDATE when event type was updated.';
COMMENT ON COLUMN apismgr.EVENTS_EVENT_TYPES.DELETED_AT IS 'Stores SYSDATE when event type was deleted.';

CREATE TABLE apismgr.EVENTS_EVENT_TOPICS
(
  EVENT_TOPIC_ID  VARCHAR2(36) NOT NULL,
  NAME            VARCHAR2(256),
  CREATED_AT      DATE NOT NULL,
  UPDATED_AT      DATE,
  DELETED_AT      DATE,
  CONSTRAINT PK_EVENTS_GROUPS PRIMARY KEY
    (
      EVENT_TOPIC_ID
    )
  USING INDEX TABLESPACE INDX)
    TABLESPACE DATA
/

CREATE PUBLIC SYNONYM EVENTS_EVENT_TOPICS FOR apismgr.EVENTS_EVENT_TOPICS;

COMMENT ON TABLE apismgr.EVENTS_EVENT_TOPICS IS 'Contains event topics used in vendor calendar system.';
COMMENT ON COLUMN apismgr.EVENTS_EVENT_TOPICS.EVENT_TOPIC_ID IS 'Primary key.';
COMMENT ON COLUMN apismgr.EVENTS_EVENT_TOPICS.NAME IS 'Plain name of event topic.';
COMMENT ON COLUMN apismgr.EVENTS_EVENT_TOPICS.CREATED_AT IS 'Stores SYSDATE when event topic was created.';
COMMENT ON COLUMN apismgr.EVENTS_EVENT_TOPICS.UPDATED_AT IS 'Stores SYSDATE when event topic was updated.';
COMMENT ON COLUMN apismgr.EVENTS_EVENT_TOPICS.DELETED_AT IS 'Stores SYSDATE when event topic was deleted.';

CREATE TABLE apismgr.EVENTS_AUDIENCES
(
  AUDIENCE_ID     VARCHAR2(36) NOT NULL,
  NAME            VARCHAR2(256),
  CREATED_AT      DATE NOT NULL,
  UPDATED_AT      DATE,
  DELETED_AT      DATE,
  CONSTRAINT PK_EVENTS_GROUPS PRIMARY KEY
    (
      AUDIENCE_ID
    )
  USING INDEX TABLESPACE INDX)
    TABLESPACE DATA
/

CREATE PUBLIC SYNONYM EVENTS_AUDIENCES FOR apismgr.EVENTS_AUDIENCES;

COMMENT ON TABLE apismgr.EVENTS_AUDIENCES IS 'Contains audiences used in vendor calendar system.';
COMMENT ON COLUMN apismgr.EVENTS_AUDIENCES.AUDIENCE_ID IS 'Primary key.';
COMMENT ON COLUMN apismgr.EVENTS_AUDIENCES.NAME IS 'Plain name of audience.';
COMMENT ON COLUMN apismgr.EVENTS_AUDIENCES.CREATED_AT IS 'Stores SYSDATE when audience was created.';
COMMENT ON COLUMN apismgr.EVENTS_AUDIENCES.UPDATED_AT IS 'Stores SYSDATE when audience was updated.';
COMMENT ON COLUMN apismgr.EVENTS_AUDIENCES.DELETED_AT IS 'Stores SYSDATE when audience was deleted.';