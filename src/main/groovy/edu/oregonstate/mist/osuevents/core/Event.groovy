package edu.oregonstate.mist.osuevents.core

class Event {
    String event_id

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
    String filters
//    HashMap<String, List<String>> filters = new HashMap<String, List<String>>()
//    HashMap<String, String> customFields = new HashMap<String, String>()
    List<Instance> instances = []
}