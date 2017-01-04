package edu.oregonstate.mist.osuevents.core

import com.fasterxml.jackson.annotation.JsonFormat
import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
class Instance {
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss'Z'", timezone="UTC")
    Date startTime

    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss'Z'", timezone="UTC")
    Date endTime
}
