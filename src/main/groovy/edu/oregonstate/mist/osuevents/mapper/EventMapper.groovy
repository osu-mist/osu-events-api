package edu.oregonstate.mist.osuevents.mapper

import edu.oregonstate.mist.osuevents.core.CustomFieldEntry
import edu.oregonstate.mist.osuevents.core.Event
import edu.oregonstate.mist.osuevents.core.FilterEntry
import groovy.json.JsonSlurper
import org.skife.jdbi.v2.StatementContext
import org.skife.jdbi.v2.tweak.ResultSetMapper
import java.sql.ResultSet
import java.sql.SQLException

class EventMapper implements ResultSetMapper<Event> {

    def jsonSlurper = new JsonSlurper()

    public Event map(int i, ResultSet rs, StatementContext sc) throws SQLException {
        new Event(
                event_id: rs.getString("EVENT_ID"),
                title: rs.getString("TITLE"),
                description: rs.getString("DESCRIPTION"),
                location: rs.getString("PLACE_NAME"),
                room: rs.getString("ROOM"),
                address: rs.getString("ADDRESS"),
                city: rs.getString("CITY"),
                state: rs.getString("STATE"),
                eventURL: rs.getString("EVENT_URL"),
                photoURL: rs.getString("PHOTO_URL"),
                ticketURL: rs.getString("TICKET_URL"),
                facebookURL: rs.getString("FACEBOOK_URL"),
                cost: rs.getString("COST"),
                hashtag: rs.getString("HASHTAG"),
                keywords: rs.getString("KEYWORDS"),
                tags: rs.getString("TAGS"),
                filters: getFilters(rs.getString("FILTERS")),
                customFields: getCustomFields(rs.getString("CUSTOM_FIELDS")),
                group: rs.getString("GROUP_NAME"),
                department: rs.getString("DEPARTMENT_NAME"),
                allowsReviews: rs.getBoolean("ALLOWS_REVIEWS"),
                sponsored: rs.getBoolean("SPONSORED"),
                venuePageOnly: rs.getBoolean("VENUE_PAGE_ONLY"),
                excludeFromTrending: rs.getBoolean("EXCLUDE_FROM_TRENDING"),
                visibility: rs.getString("VISIBILITY")
        )
    }
    private List<CustomFieldEntry> getCustomFields(String rawData) {
        List<CustomFieldEntry> customFields = []
        if (rawData) {
            def customFieldData = jsonSlurper.parseText(rawData)
            customFieldData.each {
                customFields.add(new CustomFieldEntry(field: it.field, value: it.value))
            }
        }
        customFields
    }
    private List<FilterEntry> getFilters(String rawData) {
        List<FilterEntry> filters = []
        if (rawData) {
            def filterData = jsonSlurper.parseText(rawData)
            filterData.each {
                filters.add(new FilterEntry(filter: it.filter, items: it.items))
            }
        }
        filters
    }
}