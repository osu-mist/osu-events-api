# OSU Events API

This API serves as a wrapper for getting event data into a vendor calendar system in a specific format. Events are created and updated using JSON requests, and events can be retrived in JSON, ICS, or CSV formats. The CSV is specific to the vendor calendar system.

## Resources
This section will cover the basic process of creating, updating, and deleting events, as well as getting events. For a more detailed blueprint of this API, please see the [swagger specification](swagger.yaml).

### POST /events
Create an event by sending a result object in the body of a request. If you do not include an ID within the resource object, one will be created for you. Dates follow [RFC3339 formatting](https://xml2rfc.tools.ietf.org/public/rfc/html/rfc3339.html#anchor14).

Request:

	{
      "data": {
          "id": null,
          "type": "event",
          "attributes": {
            "title": "Party",
            "description": "It's a party!",
            "location": "Student Union",
            "room": "Third door on the left",
            "address": "123 Main St.",
            "city": "Nullville",
            "state": "Oregon",
            "eventURL": "www.example.com",
            "photoURL": "www.example.com",
            "ticketURL": "www.example.com",
            "facebookURL": "www.facebook.com/example",
            "cost": "Free",
            "hashtag": "nullvilleparty",
            "keywords": "party, fun, awesome",
            "tags": "activity, entertainment",
            "group": "Party Group",
            "department": "Department of Party",
            "allowsReviews": true,
            "sponsored": false,
            "venuePageOnly": false,
            "excludeFromTrending": true,
            "visibility": null,
            "customFields": [
              {
                "field": "Theme",
                "value": "Tropical Island"
              }
            ],
            "filters": [
              {
                "filter": "Event Type",
                "items": [
                  "Party"
                ]
              },
              {
                "filter": "Meals Provided",
                "items": [
                  "Lunch"
                ]
              }
            ],
            "instances": [
                {
                "id": "1",
                "start": "2017-01-13T12:00:00-08:00",
                "end": "2017-01-13T14:00:00-08:00"
              }]
          }
        }
    }

### PATCH /events/{eventID}
Update an event by sending a result object in the body of a request. Any attributes not included in the request will remain their current values. To delete an instance of an event, set the values of start and end to null.

Request:

	{
  		"data":
			{
            "id": "e116b4b4-3696-4b74-a5d5-21b2b9ad6fee",
            "type": "event",
            "attributes": {
              "room": "Second floor, third door on the left",
              "cost": "Free, but bring pizza money!",
              "instances": [
                  {
                  "id": "1",
                  "start": "2017-01-13T13:00:00-08:00",
                  "end": "2017-01-13T15:00:00-08:00"
                }]
            }
          }
     }

### DELETE /events/{eventID}
Delete a single event by eventID. Returns 204 response if successful.

### GET /events/{eventID}
Get single event by eventID. If no event is found, result object is empty. Times are returned in UTC.

### GET /events
Get all events in JSON format.

## Updating the Cache
This API maintains a local cache of custom data from the vendor calendar system.
This cache should be updated on a regular basis to ensure the data validation specific to
the /events resources is accurate. Doing a PUT request to these endpoints will update
their respective cache.

* Custom Fields /cache/update/customfields
* Departments /cache/update/departments
* Filters /cache/update/filters
* Groups /cache/update/groups
* Places /cache/update/places

## Generate Keys

HTTPS is required for Web APIs in development and production. Use `keytool(1)` to generate public and private keys.

Generate key pair and keystore:

    $ keytool \
        -genkeypair \
        -dname "CN=Jane Doe, OU=Enterprise Computing Services, O=Oregon State University, L=Corvallis, S=Oregon, C=US" \
        -ext "san=dns:localhost,ip:127.0.0.1" \
        -alias doej \
        -keyalg RSA \
        -keysize 2048 \
        -sigalg SHA256withRSA \
        -validity 365 \
        -keystore doej.keystore

Export certificate to file:

    $ keytool \
        -exportcert \
        -rfc \
        -alias "doej" \
        -keystore doej.keystore \
        -file doej.pem

Import certificate into truststore:

    $ keytool \
        -importcert \
        -alias "doej" \
        -file doej.pem \
        -keystore doej.truststore

## Gradle

This project uses the build automation tool Gradle. Use the [Gradle Wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html) to download and install it automatically:

    $ ./gradlew

The Gradle wrapper installs Gradle in the directory `~/.gradle`. To add it to your `$PATH`, add the following line to `~/.bashrc`:

    $ export PATH=$PATH:/home/user/.gradle/wrapper/dists/gradle-2.4-all/WRAPPER_GENERATED_HASH/gradle-2.4/bin

The changes will take effect once you restart the terminal or `source ~/.bashrc`.

## Tasks

List all tasks runnable from root project:

    $ gradle tasks

### IntelliJ IDEA

Generate IntelliJ IDEA project:

    $ gradle idea

Open with `File` -> `Open Project`.

### Configure

Copy [configuration-example.yaml](configuration-example.yaml) to `configuration.yaml`. Modify as necessary, being careful to avoid committing sensitive data.

### Build

Build the project:

    $ gradle build

JARs [will be saved](https://github.com/johnrengelman/shadow#using-the-default-plugin-task) into the directory `build/libs/`.

### Run

Run the project:

    $ gradle run

