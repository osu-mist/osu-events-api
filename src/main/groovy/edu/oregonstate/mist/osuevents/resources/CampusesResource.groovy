package edu.oregonstate.mist.osuevents.resources

import com.codahale.metrics.annotation.Timed
import edu.oregonstate.mist.api.Resource
import edu.oregonstate.mist.api.jsonapi.ResourceObject
import edu.oregonstate.mist.api.jsonapi.ResultObject
import edu.oregonstate.mist.osuevents.ResourceObjectBuilder
import edu.oregonstate.mist.osuevents.core.Campus
import edu.oregonstate.mist.osuevents.db.LocalistDAO
import groovy.transform.TypeChecked

import javax.annotation.security.PermitAll
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path('/calendar/campuses')
@Produces(MediaType.APPLICATION_JSON)
@PermitAll
@TypeChecked
class CampusesResource extends Resource {

    private final LocalistDAO localistDAO
    private ResourceObjectBuilder resourceObjectBuilder

    CampusesResource(LocalistDAO localistDAO, ResourceObjectBuilder resourceObjectBuilder) {
        this.localistDAO = localistDAO
        this.resourceObjectBuilder = resourceObjectBuilder

    }

    @GET
    @Timed
    @Path('{id: [0-9a-zA-Z-]+}')
    Response getCampusByID(@PathParam('id') String campusID) {
        Campus campus = localistDAO.getCampusByID(campusID)

        if (campus) {
            ResultObject resultObject = new ResultObject(
                    data: campusResourceObject(campus)
            )
            ok(resultObject).build()
        } else {
            notFound().build()
        }
    }

    @GET
    @Timed
    Response getCampuses() {
        ResultObject resultObject = new ResultObject(
                data: localistDAO.getCampuses().collect {
                    campusResourceObject(it)
                }
        )

        ok(resultObject).build()
    }

    ResourceObject campusResourceObject(Campus campus) {
        resourceObjectBuilder.buildResourceObject(campus.id, "campuses", campus)
    }

}
