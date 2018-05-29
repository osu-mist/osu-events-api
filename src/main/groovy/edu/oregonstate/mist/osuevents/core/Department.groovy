package edu.oregonstate.mist.osuevents.core

import com.fasterxml.jackson.annotation.JsonIgnore
import edu.oregonstate.mist.osuevents.db.PaginationObject

class Department {
    @JsonIgnore
    String departmentID
    String name
    String campusID
    String calendarURL
    String url
    String description

    static Department fromLocalistDepartment(
            edu.oregonstate.mist.osuevents.db.Department department) {
        new Department(
                departmentID: department.id,
                name: department.name,
                campusID: department.campusID,
                calendarURL: department.calendarURL,
                url: department.url,
                description: department.description
        )
    }
}

class PagniatedDepartments {
    List<Department> departments
    PaginationObject paginationObject
}
