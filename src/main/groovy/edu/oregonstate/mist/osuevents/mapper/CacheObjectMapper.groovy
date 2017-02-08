package edu.oregonstate.mist.osuevents.mapper

import edu.oregonstate.mist.osuevents.core.CacheObject
import org.skife.jdbi.v2.StatementContext
import org.skife.jdbi.v2.tweak.ResultSetMapper
import java.sql.ResultSet
import java.sql.SQLException

class CacheObjectMapper implements ResultSetMapper<CacheObject> {
    public CacheObject map(int i, ResultSet rs, StatementContext sc) throws SQLException {
        new CacheObject(
                id: rs.getString("ID"),
                name: rs.getString("NAME")
        )
    }
}
