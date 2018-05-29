package edu.oregonstate.mist.osuevents.core

import com.fasterxml.jackson.annotation.JsonIgnore
import edu.oregonstate.mist.osuevents.db.PaginationObject
import edu.oregonstate.mist.osuevents.db.Place

class Location {
    @JsonIgnore
    String locationID
    String name
    String campusID
    String latitude
    String longitude
    String street
    String city
    String state
    String zip
    String calendarURL
    String url
    String photoURL

    static Location fromPlace(Place place) {
        new Location(
                locationID: place.id,
                name: place.name,
                campusID: place.campusID,
                latitude: place.latitude,
                longitude: place.longitude,
                street: place.street,
                city: place.city,
                state: place.state,
                zip: place.zip,
                calendarURL: place.calendarURL,
                url: place.url,
                photoURL: place.photoURL
        )
    }
}

class PaginatedLocations {
    List<Location> locations
    PaginationObject paginationObject
}