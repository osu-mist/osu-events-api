package edu.oregonstate.mist.osuevents.resources

import edu.oregonstate.mist.api.Error
import edu.oregonstate.mist.api.Resource

class ErrorMessages {
    public final static String unknownFields = "Event contains unrecognized fields."

    public final static String parseDate = "Unable to parse date. " +
            "Dates should follow ISO 8601 specifications."

    public final static String invalidUUID = "ID is not a valid UUID. " +
            "Event ID must follow UUID structure detailed here: " +
            "https://tools.ietf.org/html/rfc4122.html"

    public final static String idExists = "Event ID already exists."

    public final static String unexpectedException = "The application encountered an exception."

    public final static String mismatchID = "ID in JSON body must match ID in path parameter"
}
