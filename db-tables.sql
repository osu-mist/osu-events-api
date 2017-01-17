CREATE TABLE apismgr.EVENTS_EVENTS 
(
  EVENT_ID              VARCHAR2(36) NOT NULL,
  TITLE                 VARCHAR2(256) NOT NULL,
  DESCRIPTION           CLOB,
  PLACE_ID              NUMBER,
  GROUP_ID              NUMBER,
  DEPARTMENT_ID         NUMBER,
  ROOM                  VARCHAR2(256),
  ADDRESS               VARCHAR2(256),
  CITY                  VARCHAR2(256),
  STATE                 VARCHAR2(256),
  EVENT_URL             VARCHAR2(256),
  PHOTO_URL             VARCHAR2(256),
  TICKET_URL            VARCHAR2(256),
  FACEBOOK_URL          VARCHAR2(256),
  COST                  VARCHAR2(768),
  HASHTAG               VARCHAR2(256),
  KEYWORDS              VARCHAR2(256),
  TAGS                  VARCHAR2(256),
  ALLOWS_REVIEWS        VARCHAR2(1),
  SPONSORED             VARCHAR2(1),
  VENUE_PAGE_ONLY       VARCHAR2(1),
  EXCLUDE_FROM_TRENDING VARCHAR2(1),
  VISIBILITY            VARCHAR2(32),
  FILTERS               CLOB,
  CUSTOM_FIELDS         CLOB,
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

COMMENT ON TABLE apismgr.EVENTS_EVENTS  IS 'Contains event data with EVENT_ID as the unique identifier.';
COMMENT ON COLUMN apismgr.EVENTS_EVENTS.EVENT_ID IS 'Primary key. 128 bit UUID. Able to be generated by client.';
COMMENT ON COLUMN apismgr.EVENTS_EVENTS.TITLE IS 'Title of event.';
COMMENT ON COLUMN apismgr.EVENTS_EVENTS.DESCRIPTION IS 'Description for an event.';
COMMENT ON COLUMN apismgr.EVENTS_EVENTS.PLACE_ID IS 'Foreign key for PLACES table.';
COMMENT ON COLUMN apismgr.EVENTS_EVENTS.GROUP_ID IS 'Foreign key for GROUPS table.';
COMMENT ON COLUMN apismgr.EVENTS_EVENTS.DEPARTMENT_ID IS 'Foreign key for DEPARTMENTS table.';
COMMENT ON COLUMN apismgr.EVENTS_EVENTS.ROOM IS 'Specific location of event within a place.';
COMMENT ON COLUMN apismgr.EVENTS_EVENTS.ADDRESS IS 'Address of event'.;
COMMENT ON COLUMN apismgr.EVENTS_EVENTS.CITY IS 'City of event.';
COMMENT ON COLUMN apismgr.EVENTS_EVENTS.STATE IS 'State of event within the United States.';
COMMENT ON COLUMN apismgr.EVENTS_EVENTS.EVENT_URL IS 'External URL of website related event.';
COMMENT ON COLUMN apismgr.EVENTS_EVENTS.PHOTO_URL IS 'Direct URL of image to be used in event page.';
COMMENT ON COLUMN apismgr.EVENTS_EVENTS.TICKET_URL IS 'External URL of website related to event tickets';
COMMENT ON COLUMN apismgr.EVENTS_EVENTS.FACEBOOK_URL IS 'External URL of Facebook event.';
COMMENT ON COLUMN apismgr.EVENTS_EVENTS.COST IS 'String explaining event cost information';
COMMENT ON COLUMN apismgr.EVENTS_EVENTS.HASHTAG IS 'Single hashtag not preceded by #';
COMMENT ON COLUMN apismgr.EVENTS_EVENTS.KEYWORDS IS 'Comma separated.';
COMMENT ON COLUMN apismgr.EVENTS_EVENTS.TAGS IS 'Comma separated.';
COMMENT ON COLUMN apismgr.EVENTS_EVENTS.ALLOWS_REVIEWS IS '1 or 0.';
COMMENT ON COLUMN apismgr.EVENTS_EVENTS.SPONSORED IS '1 or 0.';
COMMENT ON COLUMN apismgr.EVENTS_EVENTS.VENUE_PAGE_ONLY IS '1 or 0.';
COMMENT ON COLUMN apismgr.EVENTS_EVENTS.EXCLUDE_FROM_TRENDING IS '1 or 0.';
COMMENT ON COLUMN apismgr.EVENTS_EVENTS.VISIBILITY IS '"hidden" or "logged in".';
COMMENT ON COLUMN apismgr.EVENTS_EVENTS.FILTERS IS 'JSON object of filters.';
COMMENT ON COLUMN apismgr.EVENTS_EVENTS.CUSTOM_FIELDS IS 'JSON object of custom fields and values.';
COMMENT ON COLUMN apismgr.EVENTS_EVENTS.CREATED_AT IS 'Stores SYSDATE when event was created.';
COMMENT ON COLUMN apismgr.EVENTS_EVENTS.UPDATED_AT IS 'Stores SYSDATE when event was updated.';
COMMENT ON COLUMN apismgr.EVENTS_EVENTS.DELETED_AT IS 'Stores SYSDATE when event was deleted.';

CREATE TABLE apismgr.EVENTS_INSTANCES 
(
  INSTANCE_ID        NUMBER NOT NULL,
  CLIENT_INSTANCE_ID VARCHAR2(36) NOT NULL,
  EVENT_ID           VARCHAR2(36) NOT NULL,
  START_TIME         DATE NOT NULL,
  END_TIME           DATE NOT NULL,
  CONSTRAINT PK_EVENTS_INSTANCES PRIMARY KEY 
    (
      INSTANCE_ID 
    )
  USING INDEX TABLESPACE INDX)
    TABLESPACE DATA  
/

COMMENT ON TABLE apismgr.EVENTS_INSTANCES IS 'Contains instances for events. One event can have multiple instances.';
COMMENT ON COLUMN apismgr.INSTANCES.INSTANCE_ID IS 'Primary key. For internal use only, not seen by client.';
COMMENT ON COLUMN apismgr.INSTANCES.CLIENT_INSTANCE_ID IS 'Able to be generated by client.';
COMMENT ON COLUMN apismgr.INSTANCES.EVENT_ID IS 'Foreign key for EVENTS table.';
COMMENT ON COLUMN apismgr.INSTANCES.START_TIME IS 'Start date/time of instance.';
COMMENT ON COLUMN apismgr.INSTANCES.END_TIME IS 'End date/time of instance.';

CREATE TABLE apismgr.EVENTS_GROUPS 
(
  GROUP_ID   NUMBER NOT NULL,
  NAME       VARCHAR2(256) NOT NULL,
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

COMMENT ON TABLE apismgr.EVENTS_GROUPS IS 'Contains groups used in vendor calendar system.';
COMMENT ON COLUMN apismgr.EVENTS_GROUPS.GROUP_ID IS 'Primary key.';
COMMENT ON COLUMN apismgr.EVENTS_GROUPS.NAME IS 'Plain name of group.';
COMMENT ON COLUMN apismgr.EVENTS_GROUPS.PAGE_NAME IS 'URI name of group used in vendor calendar system.';
COMMENT ON COLUMN apismgr.EVENTS_GROUPS.CREATED_AT IS 'Stores SYSDATE when group was created.';
COMMENT ON COLUMN apismgr.EVENTS_GROUPS.UPDATED_AT IS 'Stores SYSDATE when group was updated.';
COMMENT ON COLUMN apismgr.EVENTS_GROUPS.DELETED_AT IS 'Stores SYSDATE when group was deleted.';

CREATE TABLE apismgr.EVENTS_CUSTOM_FIELDS 
(
  CUSTOM_FIELD_ID NUMBER NOT NULL,
  NAME            VARCHAR2(256) NOT NULL,
  CREATED_AT      DATE NOT NULL,
  UPDATED_AT      DATE,
  DELETED_AT      DATE,
  CONSTRAINT PK_EVENTS_CUSTOM_FIELDS PRIMARY KEY 
    (
      CUSTOM_FIELD_ID 
    )
  USING INDEX TABLESPACE INDX)
    TABLESPACE DATA
/

COMMENT ON TABLE apismgr.EVENTS_CUSTOM_FIELDS  IS 'Contains custom fields used in vendor calendar system.';
COMMENT ON COLUMN apismgr.EVENTS_CUSTOM_FIELDS.NAME IS 'Name of custom field.';
COMMENT ON COLUMN apismgr.EVENTS_CUSTOM_FIELDS.CREATED_AT IS 'Stores SYSDATE when field was created.';
COMMENT ON COLUMN apismgr.EVENTS_CUSTOM_FIELDS.UPDATED_AT IS 'Stores SYSDATE when field was updated.';
COMMENT ON COLUMN apismgr.EVENTS_CUSTOM_FIELDS.DELETED_AT IS 'Stores SYSDATE when field was deleted.';

CREATE TABLE apismgr.EVENTS_PLACES 
(
  PLACE_ID   NUMBER NOT NULL,
  NAME       VARCHAR2(256) NOT NULL,
  CREATED_AT DATE NOT NULL,
  UPDATED_AT DATE,
  DELETED_AT DATE,
  CONSTRAINT PK_EVENTS_PLACES PRIMARY KEY 
    (
      PLACE_ID 
    )
  USING INDEX TABLESPACE INDX)
    TABLESPACE DATA
/

COMMENT ON TABLE apismgr.EVENTS_PLACES IS 'Stores places pulled from vendor calendar system.';
COMMENT ON COLUMN apismgr.EVENTS_PLACES.PLACE_ID IS 'Primary key.';
COMMENT ON COLUMN apismgr.EVENTS_PLACES.NAME IS 'Name of place/building.';
COMMENT ON COLUMN apismgr.EVENTS_PLACES.CREATED_AT IS 'Stores SYSDATE when place was created.';
COMMENT ON COLUMN apismgr.EVENTS_PLACES.UPDATED_AT IS 'Stores SYSDATE when place was updated.';
COMMENT ON COLUMN apismgr.EVENTS_PLACES.DELETED_AT IS 'Stores SYSDATE when place was deleted.';

CREATE TABLE apismgr.EVENTS_DEPARTMENTS 
(
  DEPARTMENT_ID NUMBER NOT NULL,
  NAME          VARCHAR2(256) NOT NULL,
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

COMMENT ON TABLE apismgr.EVENTS_DEPARTMENTS IS 'Contains departments used in vendor calendar system.';
COMMENT ON COLUMN apismgr.EVENTS_DEPARTMENTS.DEPARTMENT_ID IS 'Primary key.';
COMMENT ON COLUMN apismgr.EVENTS_DEPARTMENTS.NAME IS 'Plain name of department.';
COMMENT ON COLUMN apismgr.EVENTS_DEPARTMENTS.PAGE_NAME IS 'URI name of department used in vendor calendar system.';
COMMENT ON COLUMN apismgr.EVENTS_DEPARTMENTS.CREATED_AT IS 'Stores SYSDATE when department was created.';
COMMENT ON COLUMN apismgr.EVENTS_DEPARTMENTS.UPDATED_AT IS 'Stores SYSDATE when department was updated.';
COMMENT ON COLUMN apismgr.EVENTS_DEPARTMENTS.DELETED_AT IS 'Stores SYSDATE when department was deleted.';

CREATE TABLE apismgr.EVENTS_FILTERS 
(
FILTER_ID  NUMBER NOT NULL,
NAME       VARCHAR2(256) NOT NULL,
CREATED_AT DATE NOT NULL,
UPDATED_AT DATE,
DELETED_AT DATE,
CONSTRAINT PK_EVENTS_FILTERS PRIMARY KEY 
  (
    FILTER_ID 
  )
  USING INDEX TABLESPACE INDX)
    TABLESPACE DATA
/

COMMENT ON TABLE apismgr.EVENTS_FILTERS IS 'Stores names of filters used for events in vendor calendar system.';
COMMENT ON COLUMN apismgr.EVENTS_FILTERS.FILTER_ID IS 'Primary key.';
COMMENT ON COLUMN apismgr.EVENTS_FILTERS.NAME IS 'Name of filter.';
COMMENT ON COLUMN apismgr.EVENTS_FILTERS.CREATED_AT IS 'Stores SYSDATE when filter was created.';
COMMENT ON COLUMN apismgr.EVENTS_FILTERS.UPDATED_AT IS 'Stores SYSDATE when filter was updated.';
COMMENT ON COLUMN apismgr.EVENTS_FILTERS.DELETED_AT IS 'Stores SYSDATE when filter was deleted.';

CREATE TABLE apismgr.EVENTS_FILTER_ITEMS 
(
  ITEM_ID    NUMBER NOT NULL,
  FILTER_ID  NUMBER NOT NULL,
  NAME       VARCHAR2(256) NOT NULL,
  CREATED_AT DATE NOT NULL,
  UPDATED_AT DATE,
  DELETED_AT DATE,
  CONSTRAINT PK_EVENTS_FILTER_ITEMS PRIMARY KEY 
    (
      ITEM_ID 
    )
  USING INDEX TABLESPACE INDX)
    TABLESPACE DATA
/

COMMENT ON TABLE apismgr.EVENTS_FILTER_ITEMS IS 'Stores name of items that are contained in filters.';
COMMENT ON COLUMN apismgr.EVENTS_FILTER_ITEMS.ITEM_ID IS 'Primary key.';
COMMENT ON COLUMN apismgr.EVENTS_FILTER_ITEMS.FILTER_ID IS 'Foreign key for EVENTS_FILTERS table.';
COMMENT ON COLUMN apismgr.EVENTS_FILTER_ITEMS.NAME IS 'Name of filter item.';
COMMENT ON COLUMN apismgr.EVENTS_FILTER_ITEMS.CREATED_AT IS 'Stores SYSDATE when filter item was created.';
COMMENT ON COLUMN apismgr.EVENTS_FILTER_ITEMS.UPDATED_AT IS 'Stores SYSDATE when filter item was updated.';
COMMENT ON COLUMN apismgr.EVENTS_FILTER_ITEMS.DELETED_AT IS 'Stores SYSDATE when filter item was deleted.';

CREATE SEQUENCE apismgr.events_instance_seq
  MINVALUE 1
  START WITH 1
  INCREMENT BY 1
  NOCACHE;
