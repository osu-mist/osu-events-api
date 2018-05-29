package edu.oregonstate.mist.osuevents

import edu.oregonstate.mist.osuevents.core.Event
import edu.oregonstate.mist.osuevents.core.Instance

import java.time.ZonedDateTime

class EventTest {
    public static Event validSampleEvent = new Event(
            eventID: "some-nasty-uuid-here",
            title: "Unit Testing",
            description: "Do unit testing.",
            locationID: "1337",
            otherLocationName: null, //if a location ID is used, this field should be null
            room: "Down the hall and to the left.",
            address: null, //if a location ID is used, this field should be null
            city: null, //if a location ID is used, this field should be null
            state: null, //if a location ID is used, this field should be null
            countyID: "Benton", //this technically doesn't need to be null if a locationID is given
            eventURL: "https://example.com/foo",
            photoURL: "https://example.com/foo.jpg",
            ticketURL: "https://someticketplace.com/foo",
            ticketCost: "This can be any string.",
            hashtag: "foo",
            keywords: ["foo", "bar", "eggplant"],
            tags: ["unit-testing", "testing"],
            allowsReviews: true,
            allowUserActivity: true,
            departmentIDs: ["1", "2", "3"],
            contactName: "Jared",
            contactEmail: "jared@foo.com",
            contactPhone: "555-555-5555",
            eventTypeIDs: ["2", "1"],
            eventTopicIDs: ["2", "3"],
            audienceIDs: ["3", "4"],
            instances: [
                    new Instance(
                            start: ZonedDateTime.now(),
                            end: ZonedDateTime.now().plusHours(1)
                    ),
                    new Instance(
                            start: ZonedDateTime.now().plusDays(1),
                            end: null
                    )
            ]
    )
}
