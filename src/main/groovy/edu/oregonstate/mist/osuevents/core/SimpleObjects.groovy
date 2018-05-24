package edu.oregonstate.mist.osuevents.core

import com.fasterxml.jackson.annotation.JsonIgnore
import edu.oregonstate.mist.osuevents.db.Filter

class EventTopic extends SimpleFilterObject {
    static EventTopic fromFilter(Filter filter) {
        new EventTopic(
                id: filter.filterID,
                name: filter.name,
                parentID: filter.parentID
        )
    }
}

class EventType extends SimpleFilterObject {
    static EventType fromFilter(Filter filter) {
        new EventType(
                id: filter.filterID,
                name: filter.name,
                parentID: filter.parentID
        )
    }
}

class Audience extends SimpleFilterObject {
    static Audience fromFilter(Filter filter) {
        new Audience(
                id: filter.filterID,
                name: filter.name,
                parentID: filter.parentID
        )
    }
}

class County extends SimpleFilterObject {
    static County fromFilter(Filter filter) {
        new County(
                id: filter.filterID,
                name: filter.name,
                parentID: filter.parentID
        )
    }
}

abstract class SimpleFilterObject {
    @JsonIgnore
    String id
    String name
    String parentID
}
