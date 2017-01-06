package edu.oregonstate.mist.osuevents.core

import com.fasterxml.jackson.annotation.JsonFormat

class Instance {
    String id

    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss'Z'", timezone="UTC")
    Date start

    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss'Z'", timezone="UTC")
    Date end
}
