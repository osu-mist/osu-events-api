package edu.oregonstate.mist.osuevents

import edu.oregonstate.mist.osuevents.resources.CSVHelperFunctions
import edu.oregonstate.mist.api.AuthenticatedUser
import edu.oregonstate.mist.api.jsonapi.ResourceObject
import edu.oregonstate.mist.api.jsonapi.ResultObject
import edu.oregonstate.mist.osuevents.core.Event
import edu.oregonstate.mist.osuevents.core.Instance
import edu.oregonstate.mist.osuevents.db.EventsDAO
import edu.oregonstate.mist.osuevents.resources.ErrorMessages
import edu.oregonstate.mist.osuevents.resources.EventsResource
import groovy.mock.interceptor.MockFor
import io.dropwizard.testing.junit.DropwizardAppRule
import org.junit.ClassRule
import org.junit.Test
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

import static java.util.UUID.randomUUID

class EventsResourceTest {
    static def user = new AuthenticatedUser('nobody')

    static ResultObject basicEvent(String id = randomUUID() as String) {
        new ResultObject(
                data: new ResourceObject(
                        id: id,
                        attributes: new Event (
                                title: 'Unit Test Event',
                                description: 'Come enjoy some structured unit testing.',
                                room: 'Some Place in Memory',
                                cost: 'Some CPU threads',
                                allowsReviews: true,
                                sponsored: false,
                                visibility: 'Developers'
                        )
                )
        )
    }

    @ClassRule
    public static final DropwizardAppRule<OSUEventsConfiguration> APPLICATION =
            new DropwizardAppRule<OSUEventsConfiguration>(
                    OSUEvents.class,
                    new File("configuration.yaml").absolutePath)

    @Test
    public void testCSVDateFormat() {
        ZoneId csvTimeZone = ZoneId.of("America/Los_Angeles")
        ZonedDateTime inputDate = ZonedDateTime.of(2012, 11, 20, 12, 0, 0, 0, ZoneId.of("UTC"))

        DateTimeFormatter csvDateFormat = DateTimeFormatter
                .ofPattern("MM/dd/yyyy hh:mm a")
                .withZone(csvTimeZone)

        String csvDate = CSVHelperFunctions.getCSVDate(inputDate, csvTimeZone) +
                " " +
                CSVHelperFunctions.getCSVTime(inputDate, csvTimeZone)
        
        ZonedDateTime csvParsedDate = ZonedDateTime.parse(csvDate, csvDateFormat)

        assert csvParsedDate == inputDate.withZoneSameInstant(csvTimeZone)
    }

    @Test
    public void testGetById() {
        def mock = new MockFor(EventsDAO)

        ResourceObject event = basicEvent().data
        mock.demand.getById() { event }

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
        assert getByIdResponse.entity.data == [event]

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

        ResultObject event = basicEvent("badID1234")

        def dao = mock.proxyInstance()
        def resource = new EventsResource(dao)
        def createEventResponse = resource.createEvent(user, event)

        assert createEventResponse.status == 400
        assert createEventResponse.entity.size() == 1
        assert createEventResponse.entity.developerMessage == [ErrorMessages.invalidUUID]

    }

    @Test
    public void testExistingID() {
        def mock = new MockFor(EventsDAO)
        mock.demand.getById() { true }

        ResultObject event = basicEvent()

        def dao = mock.proxyInstance()
        def resource = new EventsResource(dao)
        def createEventResponse = resource.createEvent(user, event)

        assert createEventResponse.status == 400
        assert createEventResponse.entity.size() == 1
        assert createEventResponse.entity.developerMessage == [ErrorMessages.idExists]
    }

    @Test
    public void testNonMatchingIDs() {
        def mock = new MockFor(EventsDAO)
        mock.demand.getById() { true }

        String pathID = "eventID"
        ResultObject event = basicEvent()

        def dao = mock.proxyInstance()
        def resource = new EventsResource(dao)
        def updateEventResponse = resource.updateEvent(user, pathID, event)

        assert updateEventResponse.status == 400
        assert updateEventResponse.entity.size() == 1
        assert updateEventResponse.entity.developerMessage == [ErrorMessages.mismatchID]
    }

    @Test
    public void testBadFields() {
        def mock = new MockFor(EventsDAO)
        mock.demand.getById() { false }

        ResultObject badEvent = basicEvent()
        def badData = [badField: 'bad data', badNumberField: 3453]
        badEvent.data = badData

        def dao = mock.proxyInstance()
        def resource = new EventsResource(dao)
        def createEventResponse = resource.createEvent(user, badEvent)

        assert createEventResponse.status == 400
        assert createEventResponse.entity.size() == 1
        assert createEventResponse.entity.developerMessage == [ErrorMessages.unknownFields]
    }
}
