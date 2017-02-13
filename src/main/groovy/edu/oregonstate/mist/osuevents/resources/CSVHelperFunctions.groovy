package edu.oregonstate.mist.osuevents.resources

import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class CSVHelperFunctions {
    private static String csvDateFormat(String pattern,
                                        ZonedDateTime dateTime,
                                        ZoneId backendTimeZone) {
        DateTimeFormatter csvDateFormatter = DateTimeFormatter
                .ofPattern(pattern)
                .withZone(backendTimeZone)

        dateTime.format(csvDateFormatter)
    }

    public static String getCSVDate(ZonedDateTime dateTime, ZoneId backendTimeZone) {
        csvDateFormat("MM/dd/yyyy", dateTime, backendTimeZone)
    }

    public static String getCSVTime(ZonedDateTime dateTime, ZoneId backendTimeZone) {
        csvDateFormat("hh:mm a", dateTime, backendTimeZone)
    }
}
