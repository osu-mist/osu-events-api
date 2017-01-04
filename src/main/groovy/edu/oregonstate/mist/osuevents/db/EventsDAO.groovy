package edu.oregonstate.mist.osuevents.db

import edu.oregonstate.mist.osuevents.core.Event
import edu.oregonstate.mist.osuevents.mapper.EventMapper
import org.skife.jdbi.v2.sqlobject.Bind
import org.skife.jdbi.v2.sqlobject.SqlQuery
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper

@RegisterMapper(EventMapper)
public interface EventsDAO extends Closeable {

    /**
     * Get by ID
     */
    @SqlQuery("""
        select
            EVENT_ID,
            TITLE,
            DESCRIPTION
        from EVENTS where EVENT_ID=:id;
        """)
    Event getById(@Bind("id") Integer id)
}