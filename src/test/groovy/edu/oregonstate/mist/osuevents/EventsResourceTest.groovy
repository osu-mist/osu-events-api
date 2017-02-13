package edu.oregonstate.mist.osuevents

import edu.oregonstate.mist.osuevents.resources.CSVHelperFunctions
import io.dropwizard.testing.junit.DropwizardAppRule
import org.junit.ClassRule
import org.junit.Test
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class EventsResourceTest {

    @ClassRule
    public static final DropwizardAppRule<OSUEventsConfiguration> APPLICATION =
            new DropwizardAppRule<OSUEventsConfiguration>(
                    OSUEvents.class,
                    new File("configuration.yaml").absolutePath)

    @Test
    public void testCSVDateFormat() {
        ZoneId csvTimeZone = ZoneId.of("America/Los_Angeles")
        ZonedDateTime inputDate = ZonedDateTime.of(2012, 11, 20, 12, 0, 0, 0, ZoneId.of("UTC"))

        DateTimeFormatter csvDateFormat = DateTimeFormatter
                .ofPattern("MM/dd/yyyy hh:mm a")
                .withZone(csvTimeZone)

        String csvDate = CSVHelperFunctions.getCSVDate(inputDate, csvTimeZone) +
                " " +
                CSVHelperFunctions.getCSVTime(inputDate, csvTimeZone)
        
        ZonedDateTime csvParsedDate = ZonedDateTime.parse(csvDate, csvDateFormat)

        assert csvParsedDate == inputDate.withZoneSameInstant(csvTimeZone)
    }
}