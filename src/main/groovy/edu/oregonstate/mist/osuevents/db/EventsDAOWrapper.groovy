package edu.oregonstate.mist.osuevents.db

import edu.oregonstate.mist.osuevents.core.Event
import groovy.transform.InheritConstructors

import static java.util.UUID.randomUUID

class EventsDAOWrapper {

    private final EventsDAO eventsDAO

    EventsDAOWrapper(EventsDAO eventsDAO) {
        this.eventsDAO = eventsDAO
    }

    List<Event> getEvents() {
        List<Event> events = eventsDAO.getEvents(null)

        events.each {
            it.instances = eventsDAO.getInstances(it.eventID)
        }
    }

    Event getEventByID(String eventID) {
        if (!eventID) {
            throw new EventsDAOWrapperException("Cannot get an event with a null eventID.")
        }

        def event = eventsDAO.getEvents(eventID)

        if (event) {
            Event singleEvent = event?.get(0)
            singleEvent.instances = eventsDAO.getInstances(eventID)

            singleEvent
        } else {
            null
        }
    }

    Event createEvent(Event event) {
        event.eventID = randomUUID() as String

        //create the event in eventsDAO
        eventsDAO.createEvent(event)
        createInstances(event)

        getEventByID(event.eventID)
    }

    Event updateEvent(Event event) {
        if (!event.eventID) {
            throw new EventsDAOWrapperException(
                    "Event object must contain an eventID in order to update it.")
        }

        eventsDAO.updateEvent(event)
        eventsDAO.deleteInstances(event.eventID)
        createInstances(event)

        getEventByID(event.eventID)
    }

    void deleteEvent(String eventID) {
        eventsDAO.deleteEvent(eventID)
    }

    private void createInstances(Event event) {
        event.instances.each {
            eventsDAO.createInstance(it, event.eventID)
        }
    }
}

@InheritConstructors
class EventsDAOWrapperException extends Exception {}