package edu.oregonstate.mist.osuevents

import java.text.SimpleDateFormat

class Time {
    public static SimpleDateFormat dateInputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")

    public static SimpleDateFormat dateOutputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    /**
     * Prepares raw date format given in a JSON body to a format
     * usable by a SQL INSERT statement. Converts timezone.
     * @param inputDate
     * @return outputDate
     */
    public static String formatForDB (String inputDate) {

        dateOutputFormat.setTimeZone(TimeZone.getTimeZone("UTC"))

        Date convertedDate = dateInputFormat.parse(inputDate)
        dateOutputFormat.format(convertedDate)
    }
}