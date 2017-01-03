CREATE TABLE EVENTS 
(
  EVENT_ID CHAR(32) NOT NULL 
, TITLE VARCHAR2(256) NOT NULL 
, DESCRIPTION CLOB 
, PLACE_ID INTEGER 
, GROUP_ID INTEGER 
, DEPARTMENT_ID INTEGER 
, ROOM VARCHAR2(256) 
, ADDRESS VARCHAR2(256) 
, CITY VARCHAR2(256) 
, STATE VARCHAR2(256) 
, EVENT_URL VARCHAR2(256) 
, PHOTO_URL VARCHAR2(256) 
, TICKET_URL VARCHAR2(256) 
, COST VARCHAR2(768) 
, HASHTAG VARCHAR2(256) 
, KEYWORDS VARCHAR2(256) 
, TAGS VARCHAR2(256) 
, ALLOWS_REVIEWS CHAR(1) 
, SPONSORED CHAR(1) 
, VENUE_PAGE_ONLY CHAR(1) 
, EXCLUDE_FROM_TRENDING CHAR(1) 
, VISIBILITY VARCHAR2(32) 
, FILTERS CLOB 
, CUSTOM_FIELDS CLOB 
, CREATED_AT TIMESTAMP NOT NULL 
, UPDATED_AT TIMESTAMP 
, DELETED_AT TIMESTAMP 
, CONSTRAINT EVENTS_PK PRIMARY KEY 
  (
    EVENT_ID 
  )
  ENABLE 
);

COMMENT ON COLUMN EVENTS.EVENT_ID IS '128 bit UUID. Able to be generated by client.';

COMMENT ON COLUMN EVENTS.KEYWORDS IS 'Comma separated.';

COMMENT ON COLUMN EVENTS.TAGS IS 'Comma separated.';

COMMENT ON COLUMN EVENTS.ALLOWS_REVIEWS IS 'Y or N';

COMMENT ON COLUMN EVENTS.SPONSORED IS 'Y or N';

COMMENT ON COLUMN EVENTS.VENUE_PAGE_ONLY IS 'Y or N';

COMMENT ON COLUMN EVENTS.EXCLUDE_FROM_TRENDING IS 'Y or N';

COMMENT ON COLUMN EVENTS.VISIBILITY IS '"hidden" or "logged in"';

COMMENT ON COLUMN EVENTS.FILTERS IS 'JSON object of filters';

COMMENT ON COLUMN EVENTS.CUSTOM_FIELDS IS 'JSON object of custom fields and values';

CREATE TABLE INSTANCES 
(
  INSTANCE_ID INTEGER NOT NULL 
, CLIENT_INSTANCE_ID VARCHAR2(32) NOT NULL 
, EVENT_ID CHAR(32) NOT NULL 
, START_TIME TIMESTAMP NOT NULL 
, END_TIME TIMESTAMP NOT NULL 
, CONSTRAINT INSTANCES_PK PRIMARY KEY 
  (
    INSTANCE_ID 
  )
  ENABLE 
);

COMMENT ON COLUMN INSTANCES.INSTANCE_ID IS 'For internal use only, not seen by client';

COMMENT ON COLUMN INSTANCES.CLIENT_INSTANCE_ID IS 'Able to be generated by client';

CREATE TABLE GROUPS 
(
  GROUP_ID INTEGER NOT NULL 
, NAME VARCHAR2(256) NOT NULL 
, PAGE_NAME VARCHAR2(256) 
, CREATED_AT TIMESTAMP NOT NULL 
, UPDATED_AT TIMESTAMP 
, DELETED_AT TIMESTAMP 
, CONSTRAINT GROUPS_PK PRIMARY KEY 
  (
    GROUP_ID 
  )
  ENABLE 
);

CREATE TABLE CUSTOM_FIELDS 
(
  CUSTOM_FIELD_ID INTEGER NOT NULL 
, NAME VARCHAR2(256) NOT NULL 
, CREATED_AT TIMESTAMP NOT NULL 
, UPDATED_AT TIMESTAMP 
, DELETED_AT TIMESTAMP 
, CONSTRAINT CUSTOM_FIELDS_PK PRIMARY KEY 
  (
    CUSTOM_FIELD_ID 
  )
  ENABLE 
);

CREATE TABLE PLACES 
(
  PLACE_ID INTEGER NOT NULL 
, NAME VARCHAR2(256) NOT NULL 
, CREATED_AT TIMESTAMP NOT NULL 
, UPDATED_AT TIMESTAMP 
, DELETED_AT TIMESTAMP 
, CONSTRAINT PLACES_PK PRIMARY KEY 
  (
    PLACE_ID 
  )
  ENABLE 
);

CREATE TABLE DEPARTMENTS 
(
  DEPARTMENT_ID INTEGER NOT NULL 
, NAME VARCHAR2(256) NOT NULL 
, CREATED_AT TIMESTAMP NOT NULL 
, UPDATED_AT TIMESTAMP 
, DELETED_AT TIMESTAMP 
, CONSTRAINT DEPARTMENTS_PK PRIMARY KEY 
  (
    DEPARTMENT_ID 
  )
  ENABLE 
);

CREATE TABLE FILTERS 
(
  FILTER_ID INTEGER NOT NULL 
, NAME VARCHAR2(256) NOT NULL 
, CREATED_AT TIMESTAMP NOT NULL 
, UPDATED_AT TIMESTAMP 
, DELETED_AT TIMESTAMP 
, CONSTRAINT FILTERS_PK PRIMARY KEY 
  (
    FILTER_ID 
  )
  ENABLE 
);

CREATE TABLE FILTER_ITEMS 
(
  ITEM_ID INTEGER NOT NULL 
, FILTER_ID INTEGER NOT NULL 
, NAME VARCHAR2(256) NOT NULL 
, CREATED_AT TIMESTAMP NOT NULL 
, UPDATED_AT TIMESTAMP 
, DELETED_AT TIMESTAMP 
, CONSTRAINT FILTER_ITEMS_PK PRIMARY KEY 
  (
    ITEM_ID 
  )
  ENABLE 
);