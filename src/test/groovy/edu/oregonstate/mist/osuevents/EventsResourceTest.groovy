package edu.oregonstate.mist.osuevents

import edu.oregonstate.mist.api.AuthenticatedUser
import edu.oregonstate.mist.api.jsonapi.ResourceObject
import edu.oregonstate.mist.api.jsonapi.ResultObject
import edu.oregonstate.mist.osuevents.core.Event
import edu.oregonstate.mist.osuevents.core.Instance
import edu.oregonstate.mist.osuevents.db.EventsDAO
import edu.oregonstate.mist.osuevents.resources.ErrorMessages
import edu.oregonstate.mist.osuevents.resources.EventsResource
import groovy.mock.interceptor.MockFor
import org.junit.Test
import java.time.ZoneId
import java.time.ZonedDateTime

class EventsResourceTest {
    static def user = new AuthenticatedUser('nobody')

    @Test
    public void testGetById() {
        def mock = new MockFor(EventsDAO)

        ResourceObject testEvent = new ResourceObject(
                id: 'testid',
                attributes: new Event (
                        title: 'test title',
                        room: 'test room'
                )
        )

        mock.demand.getById() { testEvent }

        ZonedDateTime start = ZonedDateTime.of(2012, 11, 20, 12, 0, 0, 0, ZoneId.of("UTC"))
        ZonedDateTime end = ZonedDateTime.of(2012, 11, 20, 13, 0, 0, 0, ZoneId.of("UTC"))

        mock.demand.getInstances() {
            List<Instance> instances = []
            instances.add(new Instance(
                    id: 'instanceid',
                    start: start,
                    end: end
            ))
            instances
        }

        def dao = mock.proxyInstance()
        def resource = new EventsResource(dao)
        def getByIdResponse = resource.getByID(user, null)

        // test overall response
        assert getByIdResponse.status == 200
        assert getByIdResponse.entity.data == [testEvent]

        // test instances
        getByIdResponse.entity.data.attributes.instances.each {
            assert it.start == [start]
            assert it.start.zone == [ZoneId.of("UTC")]
            assert it.end == [end]
            assert it.end.zone == [ZoneId.of("UTC")]
        }
    }

    @Test
    public void testDeleteById() {
        def mock = new MockFor(EventsDAO)
        mock.demand.getById() { true }
        mock.demand.deleteEvent() {}

        def dao = mock.proxyInstance()
        def resource = new EventsResource(dao)
        def deleteEventResponse = resource.deleteEvent(user, null)

        assert deleteEventResponse.entity == null
        assert deleteEventResponse.status == 204
    }

    @Test
    public void testBadUUID() {
        def mock = new MockFor(EventsDAO)
        mock.demand.getById() { false }

        ResultObject eventWithBadUUID = new ResultObject(
                data: new ResourceObject(
                        id: 'baduuid',
                        attributes: new Event()
                )
        )

        def dao = mock.proxyInstance()
        def resource = new EventsResource(dao)
        def createEventResponse = resource.createEvent(user, eventWithBadUUID)

        assert createEventResponse.status == 400
        assert createEventResponse.entity.developerMessage == [ErrorMessages.invalidUUID]

    }
}
