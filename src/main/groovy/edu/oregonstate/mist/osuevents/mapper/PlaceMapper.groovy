package edu.oregonstate.mist.osuevents.mapper

import edu.oregonstate.mist.osuevents.core.Place
import org.skife.jdbi.v2.StatementContext
import org.skife.jdbi.v2.tweak.ResultSetMapper
import java.sql.ResultSet
import java.sql.SQLException

class PlaceMapper implements ResultSetMapper<Place> {
    public Place map(int i, ResultSet rs, StatementContext sc) throws SQLException {
        new Place(
                id: rs.getString("PLACE_ID"),
                name: rs.getString("NAME")
        )
    }
}
