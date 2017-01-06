package edu.oregonstate.mist.osuevents.mapper

import edu.oregonstate.mist.osuevents.core.Instance
import org.skife.jdbi.v2.StatementContext
import org.skife.jdbi.v2.tweak.ResultSetMapper

import java.sql.ResultSet
import java.sql.SQLException

class InstanceMapper implements ResultSetMapper<Instance> {
    public Instance map(int i, ResultSet rs, StatementContext sc) throws SQLException {
        new Instance(
                id: rs.getString("CLIENT_INSTANCE_ID"),
                start: rs.getDate("START_TIME"),
                end: rs.getDate("END_TIME")
        )
    }
}