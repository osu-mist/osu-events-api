import utils
import unittest
import json
import sys
import logging
import csv


class TestStringMethods(unittest.TestCase):

    # Test POST, GET, PUT, and DELETE /calendar/events
    def test_events_combined(self):
        # Test POST /calendar/events with valid body
        server_id = utils.post_event(event_body)
        validate_response(self, server_id, 202, "events")

        valid_id = server_id.json()["data"]["id"]

        # Test GET /calendar/events/{id} with valid ID
        valid_get = utils.get_result_by_id("events", valid_id)
        validate_response(self, valid_get, 200, "events")

        # Test PUT /calendar/events/{id} with valid ID and body
        valid_put = utils.put_event(valid_id, event_body)
        validate_response(self, valid_put, 200, "events")

        # Test DELETE /calendar/events/{id} with valid ID
        valid_delete = utils.delete_event(valid_id)
        validate_response(self, valid_delete, 204)

    # Test GET /calendar/events
    def test_get_events(self):
        valid_get = utils.get_results("events")
        validate_response(self, valid_get, 200)

    # Test POST /calendar/events
    def test_post_event(self):
        # Validate time
        validate_time(self)

        valid_date = event_body["data"]["attributes"]["instances"][0]["start"]
        # Invalid date in instance
        validate_bad_list(self, "instances", [{"start": "badDate"}],
                          message="Could not parse job object"
                          )

        # Null start date in instance
        validate_bad_list(self, "instances", [{"end": valid_date}],
                          message="An instance can not have a null start time"
                          )

        # No instances
        validate_bad_list(self, "instances", None,
                          message="At least one event instance is required"
                          )

        # Start date occurs after end date
        validate_bad_list(self, "instances", [{
            "start": "2018-12-02T21:30:00Z",
            "end": "2018-12-02T21:15:00Z"
        }],
            message="start time of an instance must occur before the end time"
        )

        # Invalid campusID
        event_body["data"]["attributes"]["campusID"] = "badCampus"
        bad_campus = utils.post_event(event_body)
        validate_response(self, bad_campus, 400, message="campusID")
        event_body["data"]["attributes"]["campusID"] = None

        # Invalid audienceIDs
        validate_bad_list(self, "audienceIDs", ["badAudience"])

        # Invalid countyIDs
        validate_bad_list(self, "countyIDs", ["badCounty"])

        # Invalid departmentIDs
        validate_bad_list(self, "departmentIDs", ["badDepartment"])

        # Invalid eventTopicIDs
        validate_bad_list(self, "eventTopicIDs", ["badTopic"])

        # Invalid eventTypeIDs
        validate_bad_list(self, "eventTypeIDs", ["badType"])

        # Hashtag field contains "#"
        invalid_event_attribute(self, "hashtag", "#")

        # No title
        invalid_event_attribute(self, "title", None)

        # No description
        invalid_event_attribute(self, "description", None)

        # No contactName
        invalid_event_attribute(self, "contactName", None)

        # No contactEmail
        invalid_event_attribute(self, "contactEmail", None)

    # Test PUT /calendar/events/{id}
    def test_put_event(self):
        valid_date = event_body["data"]["attributes"]["instances"][0]["start"]
        # Invalid ID
        event_body["data"]["id"] = "invalidEventID"
        invalid_id = utils.put_event(event_body["data"]["id"], event_body)
        validate_response(self, invalid_id, 404)
        event_body["data"]["id"] = None

        # Create test event
        valid_event = utils.post_event(event_body)
        validate_response(self, valid_event, 202, "events")
        valid_id = valid_event.json()["data"]["id"]

        # Validate time
        validate_time(self, valid_id)

        # Invalid date in instance
        validate_bad_list(self, "instances", [{"start": "badDate"}],
                          valid_id,
                          "Could not parse job object",
                          )

        # Null start date in instance
        validate_bad_list(self, "instances", [{"end": valid_date}],
                          valid_id,
                          "An instance can not have a null start time",
                          )

        # No instances
        validate_bad_list(self, "instances", None,
                          valid_id,
                          "At least one event instance is required",
                          )

        # Start date occurs after end date
        validate_bad_list(self, "instances", [{
            "start": "2018-12-02T21:30:00Z",
            "end": "2018-12-02T21:15:00Z"
        }],
            valid_id,
            "start time of an instance must occur before the end time",
        )

        # Invalid campusID
        event_body["data"]["attributes"]["campusID"] = "badCampus"
        bad_campus = utils.put_event(valid_id, event_body)
        validate_response(self, bad_campus, 400, message="campusID")
        event_body["data"]["attributes"]["campusID"] = None

        # Invalid audienceIDs
        validate_bad_list(self, "audienceIDs", ["badAudience"], valid_id)

        # Invalid countyIDs
        validate_bad_list(self, "countyIDs", ["badCounty"], valid_id)

        # Invalid departmentIDs
        validate_bad_list(self, "departmentIDs", ["badDepartment"], valid_id)

        # Invalid eventTopicIDs
        validate_bad_list(self, "eventTopicIDs", ["badTopic"], valid_id)

        # Invalid eventTypeIDs
        validate_bad_list(self, "eventTypeIDs", ["badType"], valid_id)

        # Hashtag field contains "#"
        invalid_event_attribute(self, "hashtag", "#", valid_id)

        # No title
        invalid_event_attribute(self, "title", None, valid_id)

        # No description
        invalid_event_attribute(self, "description", None, valid_id)

        # No contactName
        invalid_event_attribute(self, "contactName", None, valid_id)

        # No contactEmail
        invalid_event_attribute(self, "contactEmail", None, valid_id)

        # Clean up test event
        utils.delete_event(valid_id)

    # Test GET /calendar/event-types and GET /calendar/event-types/{id}
    def test_event_types(self):
        test_get(self, "event-types")

    # Test GET /calendar/event-topics and GET /calendar/event-topics/{id}
    def test_event_topics(self):
        test_get(self, "event-topics")

    # Test GET /calendar/counties and GET /calendar/counties/{id}
    def test_counties(self):
        test_get(self, "counties")

    # Test GET /calendar/locations and GET /calendar/locations/{id}
    def test_locations(self):
        test_get(self, "locations", is_paginated=True)

    # Test GET /calendar/campuses and GET /calendar/campuses/{id}
    def test_campuses(self):
        test_get(self, "campuses", is_paginated=True)

    # Test GET /calendar/departments and GET /calendar/departments/{id}
    def test_departments(self):
        test_get(self, "departments", is_paginated=True)

    # Test GET /calendar/audiences and GET /calendar/audiences/{id}
    def test_audiences(self):
        test_get(self, "audiences")

    # Test GET /calendar/feed
    def test_feed(self):
        valid_feed = utils.get_feed()
        validate_response(self, valid_feed, 200)

        try:
            reader = csv.reader(valid_feed.text.splitlines(),
                                dialect=csv.excel_tab)
            logging.info("/calendar/feed returned {} rows of CSV".format(
                sum(1 for row in reader)
            ))
        except csv.Error:
            self.fail("calendar/feed returned invalid CSV")


# Validates utc times are equivalent in request and response and
# validates utc times response is correct utc equivalent of local times
def validate_time(self, put_id=None):
    utc = {
        "start": "1994-11-05T08:15:00Z",
        "end": "1994-11-05T08:15:30Z"
    }
    # local times are equivalent to utc times with -08:00 offset
    local = {
        "start": "1994-11-05T00:15:00-08:00",
        "end": "1994-11-05T00:15:30-08:00"
    }
    if put_id:
        validate_single_time(self, utc, utc, put_id)
        validate_single_time(self, local, utc, put_id)
    else:
        validate_single_time(self, utc, utc)
        validate_single_time(self, local, utc)


# POSTs/PUTs event with original times and
# compares response with utc equivalent times
def validate_single_time(self, original, utc, put_id=None):
    event_body["data"]["attributes"]["instances"][0] = original
    if put_id:
        res = utils.put_event(put_id, event_body)
        validate_response(self, res, 200)
    else:
        res = utils.post_event(event_body)
        validate_response(self, res, 202)
    self.assertEqual(res.json()["data"]["attributes"]["instances"][0], utc)
    # Clean up event if POST
    if not put_id:
        utils.delete_event(res.json()["data"]["id"])


def validate_response(self, response, code=None, res_type=None, message=None):
    if code:
        self.assertEqual(response.status_code, code)
    if res_type:
        self.assertEqual(response.json()["data"]["type"], res_type)
    if message:
        self.assertIn(message, response.json()[0]["developerMessage"])


def validate_bad_list(self, list_name, bad_list, put_id=None,
                      message=None):
    if message is None:
        message = list_name
    good_list = event_body["data"]["attributes"][list_name]
    event_body["data"]["attributes"][list_name] = bad_list
    if put_id:
        bad_response = utils.put_event(put_id, event_body)
    else:
        bad_response = utils.post_event(event_body)
    validate_response(self, bad_response, 400, message=message)
    event_body["data"]["attributes"][list_name] = good_list


def test_get(self, result, is_paginated=False):
    # Test GET /{result}
    valid_results = utils.get_results(result)
    validate_response(self, valid_results, 200)

    # Test pagination
    if is_paginated:
        self.assertIsNotNone(valid_results.json()["links"])
        max_pages = config["max_pages_to_validate"]
        num_validated = 0
        page_url = valid_results.json()["links"]["first"]
        while page_url is not None and num_validated < max_pages:
            size = int(page_url.split("page%5Bsize%5D=")[-1])
            response = utils.get(page_url)
            validate_response(self, response, 200)
            self.assertLessEqual(len(response.json()["data"]), size)
            num_validated += 1
            page_url = response.json()["links"]["next"]
        logging.info("Validated {num} page(s) in /calendar/{result}".format(
            num=num_validated, result=result
        ))

    # Test GET /{result}/{id} with valid ID
    try:
        valid_id = valid_results.json()["data"][0]["id"]
        valid_result = utils.get_result_by_id(result, valid_id)
        validate_response(self, valid_result, 200, result)
    # No results in GET /result so GET /result/{id} can't be
    # tested with a valid ID
    except IndexError:
        logging.warning("Can't test GET /calendar/{}/{{id}} with valid ID. "
                        "No entries found".format(result))

    # Test GET /{result}/{id} with invalid ID
    invalid_result = utils.get_result_by_id(result, "-1")
    validate_response(self, invalid_result, 404)


def invalid_event_attribute(self, attribute_name, bad_value, put_id=None):
    default_value = event_body["data"]["attributes"][attribute_name]
    event_body["data"]["attributes"][attribute_name] = bad_value
    if put_id:
        response = utils.put_event(put_id, event_body)
    else:
        response = utils.post_event(event_body)
    validate_response(self, response, 400)
    event_body["data"]["attributes"][attribute_name] = default_value


if __name__ == "__main__":
    namespace, args = utils.parse_args()
    config = json.load(open(namespace.inputfile))
    event_body = json.load(open("valid_event_body.json"))
    utils.set_url(config)
    utils.post_token(config)
    utils.set_client_id(config)
    sys.argv = args
    unittest.main()
