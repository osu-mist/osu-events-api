package edu.oregonstate.mist.osuevents.mapper

import com.fasterxml.jackson.databind.ObjectMapper
import edu.oregonstate.mist.osuevents.core.Event
import org.skife.jdbi.v2.StatementContext
import org.skife.jdbi.v2.tweak.ResultSetMapper
import java.sql.ResultSet
import java.sql.SQLException

class EventMapper implements ResultSetMapper<Event> {
    private static ObjectMapper objectMapper = new ObjectMapper()

    public Event map(int i, ResultSet rs, StatementContext sc) throws SQLException {
        new Event(
                eventID: rs.getString("EVENT_ID"),
                title: rs.getString("TITLE"),
                description: rs.getString("DESCRIPTION"),
                locationID: rs.getString("LOCATION_ID"),
                room: rs.getString("ROOM"),
                address: rs.getString("ADDRESS"),
                city: rs.getString("CITY"),
                state: rs.getString("STATE"),
                county: rs.getString("COUNTY"),
                eventURL: rs.getString("EVENT_URL"),
                photoURL: rs.getString("PHOTO_URL"),
                facebookURL: rs.getString("FACEBOOK_URL"),
                ticketURL: rs.getString("TICKET_URL"),
                ticketCost: rs.getString("TICKET_COST"),
                hashtag: rs.getString("HASHTAG"),
                keywords: parseJsonList(rs.getString("KEYWORDS")),
                tags: parseJsonList(rs.getString("TAGS")),
                groupID: rs.getString("GROUP_ID"),
                allowsReviews: rs.getBoolean("ALLOWS_REVIEWS"),
                sponsored: rs.getBoolean("SPONSORED"),
                venuePageOnly: rs.getBoolean("VENUE_PAGE_ONLY"),
                excludeFromTrending: rs.getBoolean("EXCLUDE_FROM_TRENDING"),
                allowUserActivity: rs.getBoolean("ALLOW_USER_ACTIVITY"),
                allowUserInterest: rs.getBoolean("ALLOW_USER_INTEREST"),
                departmentIDs: parseJsonList(rs.getString("DEPARTMENT_IDS")),
                contactName: rs.getString("CONTACT_NAME"),
                contactEmail: rs.getString("CONTACT_EMAIL"),
                contactPhone: rs.getString("CONTACT_PHONE"),
                eventTypeIDs: parseJsonList(rs.getString("EVENT_TYPE_IDS")),
                eventTopicIDs: parseJsonList(rs.getString("EVENT_TOPIC_IDS")),
                audienceIDs: parseJsonList(rs.getString("AUDIENCE_IDS")),
                owner: rs.getString("OWNER")
            )
    }

    private List<String> parseJsonList(String rawJson) {
        objectMapper.readValue(rawJson, List)
    }
}