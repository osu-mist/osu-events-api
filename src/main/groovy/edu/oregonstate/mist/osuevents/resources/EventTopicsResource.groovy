package edu.oregonstate.mist.osuevents.resources

import com.codahale.metrics.annotation.Timed
import edu.oregonstate.mist.api.Resource
import edu.oregonstate.mist.api.jsonapi.ResourceObject
import edu.oregonstate.mist.api.jsonapi.ResultObject
import edu.oregonstate.mist.osuevents.ResourceObjectBuilder
import edu.oregonstate.mist.osuevents.core.EventTopic
import edu.oregonstate.mist.osuevents.db.LocalistDAO
import groovy.transform.TypeChecked

import javax.annotation.security.PermitAll
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path('/calendar/event-topics')
@Produces(MediaType.APPLICATION_JSON)
@PermitAll
@TypeChecked
class EventTopicsResource extends Resource {

    private final LocalistDAO localistDAO
    private ResourceObjectBuilder resourceObjectBuilder

    EventTopicsResource(LocalistDAO localistDAO, ResourceObjectBuilder resourceObjectBuilder) {
        this.localistDAO = localistDAO
        this.resourceObjectBuilder = resourceObjectBuilder
    }

    @GET
    @Timed
    @Path('{id: [0-9a-zA-Z-]+}')
    Response getEventTopicByID(@PathParam('id') String eventTopicID) {
        EventTopic eventTopic = localistDAO.getEventTopicByID(eventTopicID)

        if (eventTopic) {
            ResultObject resultObject = new ResultObject(
                    data: eventTopicResourceObject(eventTopic)
            )
            ok(resultObject).build()
        } else {
            notFound().build()
        }
    }

    @GET
    @Timed
    Response getEventTopics() {
        ResultObject resultObject = new ResultObject(
                data: localistDAO.getEventTopics().collect {
                    eventTopicResourceObject(it)
                }
        )

        ok(resultObject).build()
    }

    ResourceObject eventTopicResourceObject(EventTopic eventTopic) {
        resourceObjectBuilder.buildResourceObject(eventTopic.id, "event-topics", eventTopic)
    }

}
