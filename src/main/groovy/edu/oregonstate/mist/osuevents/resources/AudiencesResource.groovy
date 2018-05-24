package edu.oregonstate.mist.osuevents.resources

import com.codahale.metrics.annotation.Timed
import edu.oregonstate.mist.api.Resource
import edu.oregonstate.mist.api.jsonapi.ResourceObject
import edu.oregonstate.mist.api.jsonapi.ResultObject
import edu.oregonstate.mist.osuevents.ResourceObjectBuilder
import edu.oregonstate.mist.osuevents.core.Audience
import edu.oregonstate.mist.osuevents.db.LocalistDAO
import groovy.transform.TypeChecked

import javax.annotation.security.PermitAll
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path('/calendar/audiences')
@Produces(MediaType.APPLICATION_JSON)
@PermitAll
@TypeChecked
class AudiencesResource extends Resource {

    private final LocalistDAO localistDAO
    private ResourceObjectBuilder resourceObjectBuilder

    AudiencesResource(LocalistDAO localistDAO, ResourceObjectBuilder resourceObjectBuilder) {
        this.localistDAO = localistDAO
        this.resourceObjectBuilder = resourceObjectBuilder
    }

    @GET
    @Timed
    @Path('{id: [0-9a-zA-Z-]+}')
    Response getAudienceByID(@PathParam('id') String audienceID) {
        Audience audience = localistDAO.getAudienceByID(audienceID)

        if (audience) {
            ResultObject resultObject = new ResultObject(
                    data: audienceResourceObject(audience)
            )
            ok(resultObject).build()
        } else {
            notFound().build()
        }
    }

    @GET
    @Timed
    Response getAudiences() {
        ResultObject resultObject = new ResultObject(
                data: localistDAO.getAudiences().collect {
                    audienceResourceObject(it)
                }
        )

        ok(resultObject).build()
    }

    ResourceObject audienceResourceObject(Audience audience) {
        resourceObjectBuilder.buildResourceObject(audience.id, "audiences", audience)
    }

}
