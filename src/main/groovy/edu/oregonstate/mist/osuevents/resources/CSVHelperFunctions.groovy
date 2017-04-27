package edu.oregonstate.mist.osuevents.resources

import com.opencsv.CSVWriter
import edu.oregonstate.mist.api.jsonapi.ResultObject
import edu.oregonstate.mist.osuevents.core.Event

import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class CSVHelperFunctions {
    private static String csvDateFormat(String pattern,
                                        ZonedDateTime dateTime,
                                        ZoneId backendTimeZone) {
        DateTimeFormatter csvDateFormatter = DateTimeFormatter
                .ofPattern(pattern)
                .withZone(backendTimeZone)

        dateTime.format(csvDateFormatter)
    }

    public static String getCSVDate(ZonedDateTime dateTime, ZoneId backendTimeZone) {
        csvDateFormat("MM/dd/yyyy", dateTime, backendTimeZone)
    }

    public static String getCSVTime(ZonedDateTime dateTime, ZoneId backendTimeZone) {
        csvDateFormat("hh:mm a", dateTime, backendTimeZone)
    }

    static byte[] getCSV(ResultObject events,
                         def customfields , def filters , final ZoneId backendTimezone) {

        final ArrayList<String> BASECSVHEADER =
                ["EventID","Title" , "Description" , "Date From" , "Date To" ,
                 "Start Time", "End Time" , "Location" , "Address" , "City" , "State" ,
                 "Event Website" , "Room" , "Keywords" , "Tags" , "Photo URL" ,
                 "Ticket URL" , "Cost" , "Hashtag" , "Facebook URL" , "Group" ,
                 "Department" , "Allow User Activity" , "Allow User Attendance" ,
                 "Visibility" , "Featured Tabs" , "Sponsored" , "Venue Page Only" ,
                 "Exclude From Trending"]

        ArrayList<String> csvheader = BASECSVHEADER.clone()

        filters.each {
            csvheader += it.name
        }
        customfields.each {
            csvheader += it.name
        }

        def timestamp = (new Date()).getTime()
        String csvfilename = "events_gospel" + timestamp + ".csv"
        CSVWriter writer = new CSVWriter(new BufferedWriter(new FileWriter(csvfilename)))

        writer.writeNext((String [] ) csvheader.toArray())

        //def resultObject = getResultObject(events)
        //resultObject.data.each {
        events.data.each {
            String eventid = it.id
            Event baseEvent = it.attributes as Event

            ArrayList<String> baseEventRecord =
                    [eventid, baseEvent.title, baseEvent.description,
                     "DATE_FROM_PLACE_HOLDER", "DATE_TO_PLACE_HOLDER",
                     "START_TIME_PLACE_HOLDER", "END_TIME_PLACE_HOLDER",
                     baseEvent.location, baseEvent.address, baseEvent.city, baseEvent.state,
                     baseEvent.eventURL, baseEvent.room, baseEvent.keywords, baseEvent.tags,
                     baseEvent.photoURL, baseEvent.ticketURL, baseEvent.cost, baseEvent.hashtag,
                     baseEvent.facebookURL, baseEvent.group, baseEvent.department,
                     "Allow_User_Activity", "USER_ATTENDANCE_FIELD", baseEvent.visibility,
                     "FEATURED_TABS", baseEvent.sponsored, baseEvent.venuePageOnly,
                     baseEvent.excludeFromTrending ]

            //Map over the filter items in the same order as they were added to the header.
            def filtersMapping = baseEvent.filters.collectEntries { [it.filter, it.items] }
            filters.each {
                def entry = filtersMapping[it.name]
                if(entry != null) {
                    String entryVal = filtersMapping[it.name].toString()
                    entryVal = entryVal.substring(1 , entryVal.size() - 1) //Trim '[' ']'
                    System.out.println("Filter Name: " + it.name + " Filter Items: " + entryVal)

                    baseEventRecord.add(entryVal)
                } else {
                    baseEventRecord.add("")
                }
            }

            //Map over the custom field entries in the same order as they were added
            def eventCFieldsMap = baseEvent.customFields.collectEntries {[it.field,it.value]}
            customfields.each {
                String entryVal = eventCFieldsMap[it.name]
                if(entryVal == null) {
                    entryVal = ""
                }
                baseEventRecord.add(entryVal)
            }

            //
            //List<Instance> eventInstances = eventsDAO.getInstances(eventid)
            it.attributes.instances.each {
                String[] instanceRecord = baseEventRecord.clone()

                //Replaces "DATE_FROM_PLACE_HOLDER"
                instanceRecord[3] = getCSVDate(it.start,backendTimezone)
                //Replaces "DATE_TO_PLACE_HOLDER"
                instanceRecord[4] = getCSVDate(it.end,backendTimezone)
                //Replaces "START_TIME_PLACE_HOLDER"
                instanceRecord[5] = getCSVTime(it.start, backendTimezone)
                //Replaces "END_TIME_PLACE_HOLDER"
                instanceRecord[6] = getCSVTime(it.end, backendTimezone)

                writer.writeNext(instanceRecord)
                //TODO Handle "Allow_User_Activity","USER_ATTENDANCE_FIELD", "FEATURED_TABS"
            }
        }

        writer.close()
        def theFinalCSV = new File(csvfilename)
        def finalCSVBuf = theFinalCSV.readBytes()
        theFinalCSV.delete()

        finalCSVBuf
    }
}
