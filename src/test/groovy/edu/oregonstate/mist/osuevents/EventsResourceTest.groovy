package edu.oregonstate.mist.osuevents

import edu.oregonstate.mist.api.Error
import edu.oregonstate.mist.osuevents.core.Event
import edu.oregonstate.mist.osuevents.db.EventsDAOWrapper
import edu.oregonstate.mist.osuevents.resources.EventsResource
import groovy.mock.interceptor.MockFor
import org.junit.Before
import org.junit.Test

import javax.ws.rs.core.Response

import static org.junit.Assert.assertEquals

class EventsResourceTest {
    ResourceObjectBuilder resourceObjectBuilder

    @Before
    void setResourceObjectBuilder() {
        resourceObjectBuilder = new ResourceObjectBuilder(new URI("http://example.com/foo"))
    }

    @Test
    void testGetEventByID() {
        def notFoundEventsDAOWrapper = new MockFor(EventsDAOWrapper)
        notFoundEventsDAOWrapper.demand.getEventByID() { String eventID -> null }

        EventsResource eventsResource = new EventsResource(notFoundEventsDAOWrapper.proxyInstance(),
                null, resourceObjectBuilder)

        Response notFoundResponse = eventsResource.getEventByID("something")
        assertEquals(404, notFoundResponse.status)
        assertEquals(Error.class, notFoundResponse.entity.class)

        def foundEventsDAOWrapper = new MockFor(EventsDAOWrapper)
        foundEventsDAOWrapper.demand.getEventByID() { String eventID -> EventTest.validSampleEvent }
        eventsResource = new EventsResource(foundEventsDAOWrapper.proxyInstance(),
                null, resourceObjectBuilder)

        Response goodResponse = eventsResource.getEventByID("something")
        assertEquals(200, goodResponse.status)

        def event = goodResponse.entity["data"]["attributes"]
        assertEquals(Event.class, event.class)
        assertEquals(EventTest.validSampleEvent, event)
    }
}
