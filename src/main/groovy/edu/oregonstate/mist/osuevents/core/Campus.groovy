package edu.oregonstate.mist.osuevents.core

import com.fasterxml.jackson.annotation.JsonIgnore
import edu.oregonstate.mist.osuevents.db.Community
import edu.oregonstate.mist.osuevents.db.PaginationObject

class Campus {
    @JsonIgnore
    String campusID
    String name
    String timeZone
    String calendarURL

    static Campus fromCommunity(Community community) {
        new Campus(
                campusID: community.id,
                name: community.name,
                timeZone: community.timeZone,
                calendarURL: community.calendarURL

        )
    }
}

class PaginatedCampuses {
    List<Campus> campuses
    PaginationObject paginationObject
}
