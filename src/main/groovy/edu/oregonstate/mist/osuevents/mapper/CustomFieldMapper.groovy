package edu.oregonstate.mist.osuevents.mapper

import edu.oregonstate.mist.osuevents.core.CustomField
import org.skife.jdbi.v2.StatementContext
import org.skife.jdbi.v2.tweak.ResultSetMapper
import java.sql.ResultSet
import java.sql.SQLException

class CustomFieldMapper implements ResultSetMapper<CustomField> {
    public CustomField map(int i, ResultSet rs, StatementContext sc) throws SQLException {
        new CustomField(
                identifier: rs.getString("PLACE_ID"),
                fieldName: rs.getString("NAME")
        )
    }
}
