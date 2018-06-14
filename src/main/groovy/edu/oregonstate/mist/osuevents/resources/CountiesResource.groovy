package edu.oregonstate.mist.osuevents.resources

import com.codahale.metrics.annotation.Timed
import edu.oregonstate.mist.api.Resource
import edu.oregonstate.mist.api.jsonapi.ResourceObject
import edu.oregonstate.mist.api.jsonapi.ResultObject
import edu.oregonstate.mist.osuevents.ResourceObjectBuilder
import edu.oregonstate.mist.osuevents.core.SimpleFilterObject
import edu.oregonstate.mist.osuevents.db.LocalistDAO
import groovy.transform.TypeChecked

import javax.annotation.security.PermitAll
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path('/calendar/counties')
@Produces(MediaType.APPLICATION_JSON)
@PermitAll
@TypeChecked
class CountiesResource extends Resource {

    private final LocalistDAO localistDAO
    private ResourceObjectBuilder resourceObjectBuilder

    CountiesResource(LocalistDAO localistDAO, ResourceObjectBuilder resourceObjectBuilder) {
        this.localistDAO = localistDAO
        this.resourceObjectBuilder = resourceObjectBuilder
    }

    @GET
    @Timed
    @Path('{id: [0-9a-zA-Z-]+}')
    Response getCountyByID(@PathParam('id') String countyID) {
        SimpleFilterObject county = localistDAO.getCountyByID(countyID)

        if (county) {
            ResultObject resultObject = new ResultObject(
                    data: countyResourceObject(county)
            )
            ok(resultObject).build()
        } else {
            notFound().build()
        }
    }

    @GET
    @Timed
    Response getCounties() {
        ResultObject resultObject = new ResultObject(
                data: localistDAO.getCounties().collect {
                    countyResourceObject(it)
                }
        )

        ok(resultObject).build()
    }

    ResourceObject countyResourceObject(SimpleFilterObject county) {
        resourceObjectBuilder.buildResourceObject(county.id, "counties", county)
    }

}
