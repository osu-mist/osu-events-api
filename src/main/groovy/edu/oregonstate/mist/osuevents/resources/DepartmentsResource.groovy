package edu.oregonstate.mist.osuevents.resources

import com.codahale.metrics.annotation.Timed
import edu.oregonstate.mist.api.Resource
import edu.oregonstate.mist.api.jsonapi.ResourceObject
import edu.oregonstate.mist.api.jsonapi.ResultObject
import edu.oregonstate.mist.osuevents.ResourceObjectBuilder
import edu.oregonstate.mist.osuevents.core.Department
import edu.oregonstate.mist.osuevents.core.PagniatedDepartments
import edu.oregonstate.mist.osuevents.db.LocalistDAO
import groovy.transform.TypeChecked

import javax.annotation.security.PermitAll
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path('/calendar/departments')
@Produces(MediaType.APPLICATION_JSON)
@PermitAll
@TypeChecked
class DepartmentsResource extends Resource {

    private final LocalistDAO localistDAO
    private ResourceObjectBuilder resourceObjectBuilder

    private static final String resource = "departments"
    private static final String resourcePath = "/${ResourceObjectBuilder.baseResource}/${resource}"

    DepartmentsResource(LocalistDAO localistDAO, ResourceObjectBuilder resourceObjectBuilder) {
        this.localistDAO = localistDAO
        this.resourceObjectBuilder = resourceObjectBuilder

    }

    @GET
    @Timed
    @Path('{id: [0-9a-zA-Z-]+}')
    Response getDepartmentByID(@PathParam('id') String departmentID) {
        Department department = localistDAO.getDepartmentByID(departmentID)

        if (department) {
            ResultObject resultObject = new ResultObject(
                    data: departmentResourceObject(department)
            )
            ok(resultObject).build()
        } else {
            notFound().build()
        }
    }

    @GET
    @Timed
    Response getDepartments() {
        PagniatedDepartments pagniatedDepartments = localistDAO.getDepartments(
                getPageNumber(), getPageSize())

        ResultObject resultObject = new ResultObject(
                data: pagniatedDepartments.departments.collect {
                    departmentResourceObject(it)
                },
                links: getPagniationLinkMap(resourcePath,
                        pagniatedDepartments.paginationObject.total)
        )

        ok(resultObject).build()
    }

    ResourceObject departmentResourceObject(Department department) {
        resourceObjectBuilder.buildResourceObject(department.departmentID, resource, department)
    }
}
