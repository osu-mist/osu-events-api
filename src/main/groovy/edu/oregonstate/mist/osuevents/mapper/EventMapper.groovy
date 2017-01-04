package edu.oregonstate.mist.osuevents.mapper

import edu.oregonstate.mist.osuevents.core.Event
import org.skife.jdbi.v2.StatementContext
import org.skife.jdbi.v2.tweak.ResultSetMapper

import java.sql.ResultSet
import java.sql.SQLException

class EventMapper implements ResultSetMapper<Event> {
    public Event map(int i, ResultSet rs, StatementContext sc) throws SQLException {
        new Event(
                id: rs.getString("EVENT_ID"),
                title: rs.getString("TITLE"),
                description: rs.getString("DESCRIPTION")
        )

    }

}