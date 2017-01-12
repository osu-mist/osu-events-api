package edu.oregonstate.mist.osuevents.core

import com.fasterxml.jackson.annotation.JsonFormat

import java.time.ZonedDateTime

class Instance {
    String id

    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss'Z'", timezone="UTC")
    ZonedDateTime start

    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss'Z'", timezone="UTC")
    ZonedDateTime end
}
