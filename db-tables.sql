CREATE TABLE apismgr.EVENTS_EVENTS 
(
  EVENT_ID              VARCHAR2(36) NOT NULL,
  TITLE                 VARCHAR2(256),
  DESCRIPTION           CLOB,
  LOCATION_ID           NUMBER,
  OTHER_LOCATION_NAME   VARCHAR2(256),
  ROOM                  VARCHAR2(256),
  ADDRESS               VARCHAR2(256),
  CITY                  VARCHAR2(256),
  STATE                 VARCHAR2(256),
  COUNTY_IDS            CLOB,
  CAMPUS_ID             VARCHAR2(256),
  EVENT_URL             VARCHAR2(256),
  PHOTO_URL             VARCHAR2(256),
  TICKET_URL            VARCHAR2(256),
  TICKET_COST           VARCHAR2(768),
  HASHTAG               VARCHAR2(256),
  KEYWORDS              CLOB,
  TAGS                  CLOB,
  ALLOWS_REVIEWS        VARCHAR2(1),
  ALLOW_USER_ACTIVITY   VARCHAR2(1),
  DEPARTMENT_IDS        CLOB,
  CONTACT_NAME          VARCHAR2(256),
  CONTACT_EMAIL         VARCHAR2(256),
  CONTACT_PHONE         VARCHAR2(256),
  EVENT_TYPE_IDS        CLOB,
  EVENT_TOPIC_IDS       CLOB,
  AUDIENCE_IDS          CLOB,
  VISIBILITY            VARCHAR2(256),
  OWNER                 VARCHAR2(256),
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

CREATE OR REPLACE PUBLIC SYNONYM EVENTS_EVENTS FOR apismgr.EVENTS_EVENTS;

COMMENT ON TABLE apismgr.EVENTS_EVENTS  IS 'Contains event data with EVENT_ID as the unique identifier.';
COMMENT ON COLUMN apismgr.EVENTS_EVENTS.EVENT_ID IS 'Primary key. 128 bit UUID.';
COMMENT ON COLUMN apismgr.EVENTS_EVENTS.TITLE IS 'Title of event.';
COMMENT ON COLUMN apismgr.EVENTS_EVENTS.DESCRIPTION IS 'Description for an event.';
COMMENT ON COLUMN apismgr.EVENTS_EVENTS.LOCATION_ID IS 'Foreign key for LOCATIONS table.';
COMMENT ON COLUMN apismgr.EVENTS_EVENTS.OTHER_LOCATION_NAME IS 'Arbitrary location name if LOCATION_ID is not used.';
COMMENT ON COLUMN apismgr.EVENTS_EVENTS.ROOM IS 'Specific location of event within a place.';
COMMENT ON COLUMN apismgr.EVENTS_EVENTS.ADDRESS IS 'Address of event'.;
COMMENT ON COLUMN apismgr.EVENTS_EVENTS.CITY IS 'City of event.';
COMMENT ON COLUMN apismgr.EVENTS_EVENTS.STATE IS 'State of event within the United States.';
COMMENT ON COLUMN apismgr.EVENTS_EVENTS.COUNTY_IDS IS 'County IDs associated with event.';
COMMENT ON COLUMN apismgr.EVENTS_EVENTS.CAMPUS_ID IS 'Campus ID of event.';
COMMENT ON COLUMN apismgr.EVENTS_EVENTS.EVENT_URL IS 'External URL of website related event.';
COMMENT ON COLUMN apismgr.EVENTS_EVENTS.PHOTO_URL IS 'Direct URL of image to be used in event page.';
COMMENT ON COLUMN apismgr.EVENTS_EVENTS.TICKET_URL IS 'External URL of website related to event tickets';
COMMENT ON COLUMN apismgr.EVENTS_EVENTS.TICKET_COST IS 'String explaining event cost information';
COMMENT ON COLUMN apismgr.EVENTS_EVENTS.HASHTAG IS 'Single hashtag not preceded by #';
COMMENT ON COLUMN apismgr.EVENTS_EVENTS.KEYWORDS IS 'JSON array of event keywords.';
COMMENT ON COLUMN apismgr.EVENTS_EVENTS.TAGS IS 'JSON array of event tags.';
COMMENT ON COLUMN apismgr.EVENTS_EVENTS.ALLOWS_REVIEWS IS '1 or 0.';
COMMENT ON COLUMN apismgr.EVENTS_EVENTS.ALLOW_USER_ACTIVITY IS '1 or 0.';
COMMENT ON COLUMN apismgr.EVENTS_EVENTS.DEPARTMENT_IDS IS 'JSON array of department IDs.';
COMMENT ON COLUMN apismgr.EVENTS_EVENTS.CONTACT_NAME IS 'Contact name of event organizer.';
COMMENT ON COLUMN apismgr.EVENTS_EVENTS.CONTACT_EMAIL IS 'Email of event organizer.';
COMMENT ON COLUMN apismgr.EVENTS_EVENTS.CONTACT_PHONE IS 'Phone number of event organizer.';
COMMENT ON COLUMN apismgr.EVENTS_EVENTS.EVENT_TYPE_IDS IS 'JSON array of event type IDs';
COMMENT ON COLUMN apismgr.EVENTS_EVENTS.EVENT_TOPIC_IDS IS 'JSON array of event topic IDs';
COMMENT ON COLUMN apismgr.EVENTS_EVENTS.AUDIENCE_IDS IS 'JSON array of audience IDs';
COMMENT ON COLUMN apismgr.EVENTS_EVENTS.VISIBILITY IS 'Visibility of event.';
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

CREATE OR REPLACE PUBLIC SYNONYM EVENTS_INSTANCES FOR apismgr.EVENTS_INSTANCES;

COMMENT ON TABLE apismgr.EVENTS_INSTANCES IS 'Contains instances for events. One event can have multiple instances.';
COMMENT ON COLUMN apismgr.INSTANCES.EVENT_ID IS 'Foreign key for EVENTS table.';
COMMENT ON COLUMN apismgr.INSTANCES.START_TIME IS 'Start date/time of instance.';
COMMENT ON COLUMN apismgr.INSTANCES.END_TIME IS 'End date/time of instance.';
COMMENT ON COLUMN apismgr.INSTANCES.CREATED_AT IS 'Stores SYSDATE when instance was created.';