package edu.oregonstate.mist.osuevents.resources

import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class CSVHelperFunctions {
    private static final DateTimeFormatter csvDateFormatter = DateTimeFormatter
            .ofPattern("MM/dd/yyyy")
            .withZone(ZoneId.of("America/Los_Angeles"))

    private static final DateTimeFormatter csvTimeFormatter = DateTimeFormatter
            .ofPattern("hh:mm a")
            .withZone(ZoneId.of("America/Los_Angeles"))

    public static String getCSVDate(ZonedDateTime dateTime) {
        dateTime.format(csvDateFormatter)
    }

    public static String getCSVTime(ZonedDateTime dateTime) {
        dateTime.format(csvTimeFormatter)
    }
}
