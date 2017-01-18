package edu.oregonstate.mist.osuevents.resources

class ErrorMessages {
    public final static String unknownFields = "Event contains unrecognized fields."

    public final static String parseDate = "Unable to parse date." +
            " Dates should follow ISO 8601 specifications."

    public final static String processInstance = "Unable to process instance. " +
            "Ensure instance ID is a string."

    public final static String unexpectedException = "The application encountered an exception."
}
