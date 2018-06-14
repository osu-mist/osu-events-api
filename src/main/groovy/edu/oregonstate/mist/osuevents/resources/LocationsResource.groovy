package edu.oregonstate.mist.osuevents.resources

import com.codahale.metrics.annotation.Timed
import edu.oregonstate.mist.api.Resource
import edu.oregonstate.mist.api.jsonapi.ResourceObject
import edu.oregonstate.mist.api.jsonapi.ResultObject
import edu.oregonstate.mist.osuevents.ResourceObjectBuilder
import edu.oregonstate.mist.osuevents.core.Location
import edu.oregonstate.mist.osuevents.core.PaginatedLocations
import edu.oregonstate.mist.osuevents.db.LocalistDAO
import groovy.transform.TypeChecked

import javax.annotation.security.PermitAll
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path('/calendar/locations')
@Produces(MediaType.APPLICATION_JSON)
@PermitAll
@TypeChecked
class LocationsResource extends Resource {

    private final LocalistDAO localistDAO
    private ResourceObjectBuilder resourceObjectBuilder

    private static final String resource = "locations"
    private static final String resourcePath = "/${ResourceObjectBuilder.baseResource}/${resource}"

    LocationsResource(LocalistDAO localistDAO, ResourceObjectBuilder resourceObjectBuilder) {
        this.localistDAO = localistDAO
        this.resourceObjectBuilder = resourceObjectBuilder

    }

    @GET
    @Timed
    @Path('{id: [0-9a-zA-Z-]+}')
    Response getLocationByID(@PathParam('id') String locationID) {
        Location location = localistDAO.getlocationByID(locationID)

        if (location) {
            ResultObject resultObject = new ResultObject(
                    data: locationResourceObject(location)
            )
            ok(resultObject).build()
        } else {
            notFound().build()
        }
    }

    @GET
    @Timed
    Response getLocations() {
        if (maxPageSizeExceeded()) {
            return badRequest("page[size] parameter must not exceed ${MAX_PAGE_SIZE}").build()
        }

        PaginatedLocations paginatedLocations = localistDAO.getLocations(
                getPageNumber(), getPageSize())

        ResultObject resultObject = new ResultObject(
                data: paginatedLocations.locations.collect {
                    locationResourceObject(it)
                },
                links: getPagniationLinkMap(resourcePath, paginatedLocations.paginationObject.total)
        )

        ok(resultObject).build()
    }

    ResourceObject locationResourceObject(Location location) {
        resourceObjectBuilder.buildResourceObject(location.locationID, resource, location)
    }
}
