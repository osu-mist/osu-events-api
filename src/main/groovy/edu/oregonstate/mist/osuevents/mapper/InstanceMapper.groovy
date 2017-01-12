package edu.oregonstate.mist.osuevents.mapper

import edu.oregonstate.mist.osuevents.Time
import edu.oregonstate.mist.osuevents.core.Instance
import org.skife.jdbi.v2.StatementContext
import org.skife.jdbi.v2.tweak.ResultSetMapper
import java.sql.ResultSet
import java.sql.SQLException
import java.time.ZonedDateTime

class InstanceMapper implements ResultSetMapper<Instance> {
    public Instance map(int i, ResultSet rs, StatementContext sc) throws SQLException {
        new Instance(
                id: rs.getString("CLIENT_INSTANCE_ID"),
                start: ZonedDateTime.parse(rs.getString("START_TIME"), Time.dbFormatter),
                end: ZonedDateTime.parse(rs.getString("END_TIME"), Time.dbFormatter)
        )
    }
}