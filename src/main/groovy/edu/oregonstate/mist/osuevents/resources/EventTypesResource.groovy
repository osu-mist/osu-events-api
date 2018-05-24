package edu.oregonstate.mist.osuevents.resources

import com.codahale.metrics.annotation.Timed
import edu.oregonstate.mist.api.Resource
import edu.oregonstate.mist.api.jsonapi.ResourceObject
import edu.oregonstate.mist.api.jsonapi.ResultObject
import edu.oregonstate.mist.osuevents.core.EventType
import edu.oregonstate.mist.osuevents.db.LocalistDAO
import groovy.transform.TypeChecked

import javax.annotation.security.PermitAll
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path('/calendar/event-types')
@Produces(MediaType.APPLICATION_JSON)
@PermitAll
@TypeChecked
class EventTypesResource extends Resource {

    private final LocalistDAO localistDAO

    EventTypesResource(LocalistDAO localistDAO) {
        this.localistDAO = localistDAO
    }

    @GET
    @Timed
    @Path('{id: [0-9a-zA-Z-]+}')
    Response getEventTypeByID(@PathParam('id') String eventTypeID) {
        EventType eventType = localistDAO.getEventTypeByID(eventTypeID)

        if (eventType) {
            ResultObject resultObject = new ResultObject(
                    data: eventTypeResourceObject(eventType)
            )
            ok(resultObject).build()
        } else {
            notFound().build()
        }
    }

    @GET
    @Timed
    Response getEventTypes() {
        ResultObject resultObject = new ResultObject(
                data: localistDAO.getEventTypes().collect {
                    eventTypeResourceObject(it)
                }
        )

        ok(resultObject).build()
    }

    ResourceObject eventTypeResourceObject(EventType eventType) {
        new ResourceObject(
                id: eventType.id,
                type: "event-types",
                attributes: eventType
        )
    }

}
