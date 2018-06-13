package edu.oregonstate.mist.osuevents.core

import com.fasterxml.jackson.annotation.JsonIgnore
import edu.oregonstate.mist.osuevents.db.Filter

class SimpleFilterObject {
    @JsonIgnore
    String id
    String name
    String parentID

    static SimpleFilterObject fromFilter(Filter filter) {
        new SimpleFilterObject(
                id: filter.filterID,
                name: filter.name,
                parentID: filter.parentID
        )
    }
}