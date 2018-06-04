package edu.oregonstate.mist.osuevents.mapper

import edu.oregonstate.mist.osuevents.core.Instance
import org.skife.jdbi.v2.StatementContext
import org.skife.jdbi.v2.tweak.ResultSetMapper
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Timestamp
import java.time.ZoneId
import java.time.ZonedDateTime

class InstanceMapper implements ResultSetMapper<Instance> {
    public Instance map(int i, ResultSet rs, StatementContext sc) throws SQLException {
        new Instance(
                start: parseTimestamp(rs.getTimestamp("START_TIME")),
                end: parseTimestamp(rs.getTimestamp("END_TIME"))
        )
    }

    /**
     * Helper method to convert timestamp to ZonedDateTime
     * @param timestamp
     * @return
     */
    private ZonedDateTime parseTimestamp(Timestamp timestamp) {
        if (timestamp) {
            ZonedDateTime.ofInstant(timestamp.toInstant(), ZoneId.of("UTC"))
        } else {
            null
        }
    }
}