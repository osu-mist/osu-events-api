package edu.oregonstate.mist.osuevents.resources

import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class CSVHelperFunctions {
    public static String getCSVDate(ZonedDateTime dateTime, ZoneId backendTimeZone) {
        DateTimeFormatter csvDateFormatter = DateTimeFormatter
                .ofPattern("MM/dd/yyyy")
                .withZone(backendTimeZone)

        dateTime.format(csvDateFormatter)
    }

    public static String getCSVTime(ZonedDateTime dateTime, ZoneId backendTimeZone) {
        DateTimeFormatter csvTimeFormatter = DateTimeFormatter
                .ofPattern("hh:mm a")
                .withZone(backendTimeZone)

        dateTime.format(csvTimeFormatter)
    }
}
