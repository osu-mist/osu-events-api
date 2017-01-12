package edu.oregonstate.mist.osuevents

import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class Time {

    public static DateTimeFormatter dbFormatter = DateTimeFormatter
            .ofPattern("yyyy-MM-dd HH:mm:ss")
            .withZone(ZoneId.of("UTC"))

    /**
     * Prepares raw date format given in a JSON body to a format
     * usable by a SQL INSERT statement. Converts timezone.
     * @param inputDate
     * @return outputDate
     */
    public static String formatForDB (String inputDate) {

        ZonedDateTime cleanDate = ZonedDateTime.parse(
                inputDate,
                DateTimeFormatter.ISO_OFFSET_DATE_TIME)

        cleanDate.format(dbFormatter)
    }
}