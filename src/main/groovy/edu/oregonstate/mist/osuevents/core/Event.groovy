package edu.oregonstate.mist.osuevents.core

class Event {
    String eventID

    String title
    String description

    String location
    String room
    String address
    String city
    String state

    String eventURL
    String photoURL
    String ticketURL
    String facebookURL

    String cost
    String hashtag
    String keywords
    String tags
    String group
    String department

    Boolean allowsReviews
    Boolean sponsored
    Boolean venuePageOnly
    Boolean excludeFromTrending

    String visibility
    List<CustomFieldEntry> customFields = []
    List<FilterEntry> filters = []
    List<Instance> instances = []
}