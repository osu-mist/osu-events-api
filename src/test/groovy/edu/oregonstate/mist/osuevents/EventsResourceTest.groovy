package edu.oregonstate.mist.osuevents

import edu.oregonstate.mist.api.Error
import edu.oregonstate.mist.api.jsonapi.ResourceObject
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
        def notFoundEventsDAOWrapper = getMockEventsDAOWrapper()
        notFoundEventsDAOWrapper.demand.getEventByID() { String eventID -> null }

        EventsResource eventsResource = getEventsResource(notFoundEventsDAOWrapper,
                null)

        Response notFoundResponse = eventsResource.getEventByID("something")
        assertEquals(404, notFoundResponse.status)
        assertEquals(Error.class, notFoundResponse.entity.class)

        def foundEventsDAOWrapper = getMockEventsDAOWrapper()
        foundEventsDAOWrapper.demand.getEventByID() { String eventID -> sampleEvent() }
        eventsResource = getEventsResource(foundEventsDAOWrapper, null)

        Response goodResponse = eventsResource.getEventByID("something")
        assertEquals(200, goodResponse.status)

        def event = goodResponse.entity["data"]["attributes"]
        assertEquals(Event.class, event.class)
        assertEquals(EventTest.validSampleEvent, event)
    }

    @Test
    void testGetEvents() {
        def eventsDAOWrapper = getMockEventsDAOWrapper()

        List<Event> events = [sampleEvent(), sampleEvent()]

        eventsDAOWrapper.demand.getEvents() { events }

        EventsResource eventsResource = getEventsResource(eventsDAOWrapper, null)
        Response goodResponse = eventsResource.getEvents()

        assertEquals(200, goodResponse.status)
        List<ResourceObject> returnedEvents = goodResponse.entity["data"]
        assertEquals(2, returnedEvents.size())
    }

    @Test
    void testCreateEvent() {
        def eventsDAOWrapper = getMockEventsDAOWrapper()
        eventsDAOWrapper.demand.createEvent() { Event event -> sampleEvent() }
        eventsDAOWrapper.demand.getEventByID() { String eventID -> sampleEvent() }
        getEventsResource(eventsDAOWrapper, null)
    }

    private Event sampleEvent() {
        EventTest.validSampleEvent
    }

    private MockFor getMockEventsDAOWrapper() {
        new MockFor(EventsDAOWrapper)
    }

    private EventsResource getEventsResource(MockFor eventsDAOWrapper, MockFor localistDAO) {
        new EventsResource(eventsDAOWrapper?.proxyInstance(),
                localistDAO?.proxyInstance(), resourceObjectBuilder)
    }
}
