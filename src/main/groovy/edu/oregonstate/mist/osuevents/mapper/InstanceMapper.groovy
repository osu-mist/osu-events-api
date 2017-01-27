package edu.oregonstate.mist.osuevents.mapper

import edu.oregonstate.mist.osuevents.core.Instance
import org.skife.jdbi.v2.StatementContext
import org.skife.jdbi.v2.tweak.ResultSetMapper
import java.sql.ResultSet
import java.sql.SQLException
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class InstanceMapper implements ResultSetMapper<Instance> {
    public Instance map(int i, ResultSet rs, StatementContext sc) throws SQLException {
        new Instance(
                id: rs.getString("CLIENT_INSTANCE_ID"),
                start: (rs.getString("START_TIME")) ?
                        ZonedDateTime.parse(rs.getString("START_TIME"), dbFormatter) : null,
                end: (rs.getString("END_TIME")) ?
                        ZonedDateTime.parse(rs.getString("END_TIME"), dbFormatter) : null
        )
    }
    private static DateTimeFormatter dbFormatter = DateTimeFormatter
            .ofPattern("yyyy-MM-dd HH:mm:ss")
            .withZone(ZoneId.of("UTC"))

    public static String formatForDB (String inputDate) {
        if (!inputDate || inputDate == "null") {
            return null
        }
        ZonedDateTime cleanDate = ZonedDateTime.parse(
                inputDate,
                DateTimeFormatter.ISO_OFFSET_DATE_TIME)

        cleanDate.format(dbFormatter)
    }
}