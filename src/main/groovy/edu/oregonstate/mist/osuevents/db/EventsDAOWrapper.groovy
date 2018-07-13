package edu.oregonstate.mist.osuevents.db

import edu.oregonstate.mist.osuevents.core.Event
import groovy.transform.InheritConstructors

import static java.util.UUID.randomUUID

class EventsDAOWrapper {

    private final EventsDAO eventsDAO

    EventsDAOWrapper(EventsDAO eventsDAO) {
        this.eventsDAO = eventsDAO
    }

    /**
     * Get all events.
     * @return
     */
    List<Event> getEvents(Integer changedInPastHours = null) {
        List<Event> events = eventsDAO.getEvents(null, changedInPastHours)

        events.each {
            it.instances = eventsDAO.getInstances(it.eventID)
        }
    }

    /**
     * Get single event by ID.
     * @param eventID
     * @return
     */
    Event getEventByID(String eventID) {
        if (!eventID) {
            throw new EventsDAOWrapperException("Cannot get an event with a null eventID.")
        }

        def event = eventsDAO.getEvents(eventID, null)

        if (event) {
            Event singleEvent = event?.get(0)
            singleEvent.instances = eventsDAO.getInstances(eventID)

            singleEvent
        } else {
            null
        }
    }

    /**
     * Create event, assigns a random UUID.
     * @param event
     * @return
     */
    Event createEvent(Event event) {
        event.eventID = randomUUID() as String

        //create the event in eventsDAO
        eventsDAO.createEvent(event)
        createInstances(event)

        getEventByID(event.eventID)
    }

    /**
     * Update event. Event object must include ID.
     * @param event
     * @return
     */
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

    /**
     * Delete an event by its ID.
     * @param eventID
     */
    void deleteEvent(String eventID) {
        eventsDAO.deleteEvent(eventID)
    }

    /**
     * Helper method to create instances during event creation or updating.
     * @param event
     */
    private void createInstances(Event event) {
        event.instances.each {
            eventsDAO.createInstance(it, event.eventID)
        }
    }
}

@InheritConstructors
class EventsDAOWrapperException extends Exception {}
