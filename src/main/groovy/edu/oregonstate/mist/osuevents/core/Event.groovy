package edu.oregonstate.mist.osuevents.core

class Event {
    String id

    String title
    String description

    String location
    String room
    String address
    String city
    String state

    String url
    String photoURL
    String ticketURL
    String facebookURL

    String cost
    String hashtag
    List<String> keywords = []
    List<String> tags = []
    String group
    String department

    Boolean allowsReviews
    Boolean sponsored
    Boolean venuePageOnly
    Boolean excludeFromTrending

    String visibility
    HashMap<String, List<String>> filters = new HashMap<String, List<String>>()
    HashMap<String, String> customFields = new HashMap<String, String>()
    HashMap<String, List<Instance>> instances = new HashMap<String, List<Instance>>()
}