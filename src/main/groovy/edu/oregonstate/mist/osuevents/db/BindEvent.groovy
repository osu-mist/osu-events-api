package edu.oregonstate.mist.osuevents.db

import com.fasterxml.jackson.databind.ObjectMapper
import edu.oregonstate.mist.osuevents.core.Event
import org.skife.jdbi.v2.SQLStatement
import org.skife.jdbi.v2.sqlobject.Binder
import org.skife.jdbi.v2.sqlobject.BinderFactory
import org.skife.jdbi.v2.sqlobject.BindingAnnotation

import java.lang.annotation.Annotation
import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * Custom binder for event object to handle formatting lists as json arrays.
 */
@BindingAnnotation(BindEvent.EventBinderFactor.class)
@Retention(RetentionPolicy.RUNTIME)
@Target([ElementType.PARAMETER])
public @interface BindEvent {
    public static class EventBinderFactor implements BinderFactory {
        private static ObjectMapper objectMapper = new ObjectMapper()

        /**
         * Format a list of strings as a json array of strings.
         * @param list
         * @return
         */
        private static String formatJsonList(List<String> list) {
            objectMapper.writeValueAsString(list)
        }

        public Binder build(Annotation annotation) {
            new Binder<BindEvent, Event>() {
                public void bind(SQLStatement q, BindEvent bind, Event event) {
                    q.bind("eventID", event.eventID)
                    q.bind("title", event.title)
                    q.bind("description", event.description)
                    q.bind("locationID", event.locationID)
                    q.bind("otherLocationName", event.otherLocationName)
                    q.bind("room", event.room)
                    q.bind("address", event.address)
                    q.bind("city", event.city)
                    q.bind("state", event.state)
                    q.bind("countyIDs", formatJsonList(event.countyIDs))
                    q.bind("campusID", event.campusID)
                    q.bind("eventURL", event.eventURL)
                    q.bind("photoURL", event.photoURL)
                    q.bind("ticketURL", event.ticketURL)
                    q.bind("ticketCost", event.ticketCost)
                    q.bind("hashtag", event.hashtag)
                    q.bind("keywords", formatJsonList(event.keywords))
                    q.bind("tags", formatJsonList(event.tags))
                    q.bind("allowsReviews", event.allowsReviews)
                    q.bind("allowUserActivity", event.allowUserActivity)
                    q.bind("departmentIDs", formatJsonList(event.departmentIDs))
                    q.bind("contactName", event.contactName)
                    q.bind("contactEmail", event.contactEmail)
                    q.bind("contactPhone", event.contactPhone)
                    q.bind("eventTypeIDs", formatJsonList(event.eventTypeIDs))
                    q.bind("eventTopicIDs", formatJsonList(event.eventTopicIDs))
                    q.bind("audienceIDs", formatJsonList(event.audienceIDs))
                    q.bind("visibility", event.visibility)
                    q.bind("owner", event.owner)
                }
            }
        }
    }
}
