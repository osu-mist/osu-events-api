package edu.oregonstate.mist.osuevents.mapper

import edu.oregonstate.mist.osuevents.core.Instance
import groovy.json.JsonSlurper
import org.skife.jdbi.v2.StatementContext
import org.skife.jdbi.v2.tweak.ResultSetMapper
import java.sql.ResultSet
import java.sql.SQLException
import java.text.SimpleDateFormat

class InstanceMapper implements ResultSetMapper<Instance> {
    public Instance map(int i, ResultSet rs, StatementContext sc) throws SQLException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss", Locale.ENGLISH)
        new Instance(
                id: rs.getString("CLIENT_INSTANCE_ID"),
                start: format.parse(rs.getString("START_TIME")),
                end: format.parse(rs.getString("START_TIME"))
        )
    }
}