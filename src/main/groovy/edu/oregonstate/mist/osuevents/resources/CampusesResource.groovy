package edu.oregonstate.mist.osuevents.resources

import com.codahale.metrics.annotation.Timed
import edu.oregonstate.mist.api.Resource
import edu.oregonstate.mist.api.jsonapi.ResourceObject
import edu.oregonstate.mist.api.jsonapi.ResultObject
import edu.oregonstate.mist.osuevents.ResourceObjectBuilder
import edu.oregonstate.mist.osuevents.core.Campus
import edu.oregonstate.mist.osuevents.core.PaginatedCampuses
import edu.oregonstate.mist.osuevents.db.LocalistDAO
import edu.oregonstate.mist.osuevents.db.PaginationObject
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

    private static final String resource = "campuses"
    private static final String resourcePath = "/${ResourceObjectBuilder.baseResource}/${resource}"

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
        PaginatedCampuses paginatedCampuses = localistDAO.getCampuses(
                getPageNumber(), getPageSize())

        ResultObject resultObject = new ResultObject(
                data: paginatedCampuses.campuses.collect {
                    campusResourceObject(it)
                },
                links: getPagiationLinks(paginatedCampuses.paginationObject)
        )

        ok(resultObject).build()
    }

    ResourceObject campusResourceObject(Campus campus) {
        resourceObjectBuilder.buildResourceObject(campus.campusID, "campuses", campus)
    }

    private Map getPagiationLinks(PaginationObject paginationObject) {
        def links = [:]

        links['self'] = getPaginationUrl(['pageNumber': getPageNumber(), 'pageSize': getPageSize()],
                resourcePath)

        links['first'] = getPaginationUrl(['pageNumber': 1, 'pageSize': getPageSize()],
                resourcePath)

        links['last'] = getPaginationUrl(['pageNumber': paginationObject.total,
                                          'pageSize': getPageSize()],
                resourcePath)

        if (getPageNumber() <= 1) {
            links['prev'] = null
        } else {
            links['prev'] = getPaginationUrl(['pageNumber': getPageNumber() - 1,
                                              'pageSize': getPageSize()],
                    resourcePath)
        }

        if (getPageNumber() >= paginationObject.total) {
            links['next'] = null
        } else {
            links['next'] = getPaginationUrl(['pageNumber': getPageNumber() + 1,
                                              'pageSize': getPageSize()],
                    resourcePath)
        }

        links
    }
}
