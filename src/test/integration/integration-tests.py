import utils
import unittest
import json
import sys


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
        # Invalid date in instance
        good_date = event_body["data"]["attributes"]["instances"][0]["start"]
        event_body["data"]["attributes"]["instances"][0]["start"] = "badDate"
        bad_date = utils.post_event(event_body)
        validate_response(self, bad_date, 400, message="Could not parse job")
        event_body["data"]["attributes"]["instances"][0]["start"] = good_date

        # Invalid campusID
        event_body["data"]["attributes"]["campusID"] = "badCampus"
        bad_campus = utils.post_event(event_body)
        validate_response(self, bad_campus, 400, message="campusID")
        event_body["data"]["attributes"]["campusID"] = None

        # Invalid audienceIDs
        validate_bad_list(self, "audienceIDs")

        # Invalid countyIDs
        validate_bad_list(self, "countyIDs")

        # Invalid departmentIDs
        validate_bad_list(self, "departmentIDs")

        # Invalid eventTopicIDs
        validate_bad_list(self, "eventTopicIDs")

        # Invalid eventTypeIDs
        validate_bad_list(self, "eventTypeIDs")

    # Test PUT /calendar/events/{id}
    def test_put_event(self):
        # Invalid ID
        event_body["data"]["id"] = "invalidEventID"
        invalid_id = utils.put_event(event_body["data"]["id"], event_body)
        validate_response(self, invalid_id, 404)
        event_body["data"]["id"] = None

        # Create test event
        valid_event = utils.post_event(event_body)
        valid_id = valid_event.json()["data"]["id"]

        # Invalid audienceIDs
        validate_bad_list(self, "audienceIDs", valid_id)
        # Invalid countyIDs
        validate_bad_list(self, "countyIDs", valid_id)
        # Invalid departmentIDs
        validate_bad_list(self, "departmentIDs", valid_id)
        # Invalid eventTopicIDs
        validate_bad_list(self, "eventTopicIDs", valid_id)
        # Invalid eventTypeIDs
        validate_bad_list(self, "eventTypeIDs", valid_id)

        # Clean up test event
        utils.delete_event(valid_id)

    # Test GET /calendar/event-types and GET /calendar/event-types/{id}
    def test_event_types(self):
        test_get(self, "event-types")

    # Test GET /calendar/event-topics and GET /calendar/event-topics/{id}
    def test_event_topics(self):
        test_get(self, "event-topics")

    # Test GET /calendar/locations and GET /calendar/locations/{id}
    def test_locations(self):
        test_get(self, "locations")

    # Test GET /calendar/counties and GET /calendar/counties/{id}
    def test_counties(self):
        test_get(self, "counties")

    # Test GET /calendar/campuses and GET /calendar/campuses/{id}
    def test_campuses(self):
        test_get(self, "campuses")

    # Test GET /calendar/departments and GET /calendar/departments/{id}
    def test_departments(self):
        test_get(self, "departments")

    # Test GET /calendar/audiences and GET /calendar/audiences/{id}
    def test_audiences(self):
        test_get(self, "audiences")


def validate_response(self, response, code=None, res_type=None, message=None):
    if code:
        self.assertEqual(response.status_code, code)
    if res_type:
        self.assertEqual(response.json()["data"]["type"], res_type)
    if message:
        self.assertIn(message, response.json()[0]["developerMessage"])


def validate_bad_list(self, list_name, put_id=None):
    event_body["data"]["attributes"][list_name] = []
    event_body["data"]["attributes"][list_name].append("badItem")
    if put_id:
        bad_response = utils.put_event(put_id, event_body)
    else:
        bad_response = utils.post_event(event_body)
    validate_response(self, bad_response, 400, message=list_name)
    event_body["data"]["attributes"][list_name] = None


def test_get(self, result):
    # Test GET /{result}
    valid_results = utils.get_results(result)
    validate_response(self, valid_results, 200)

    # Test GET /{result}/{id} with valid ID
    try:
        valid_id = valid_results.json()["data"][0]["id"]
        valid_result = utils.get_result_by_id(result, valid_id)
        validate_response(self, valid_result, 200, result)
    # No results in GET /result so GET /result/{id} can't be
    # tested with a valid ID
    except IndexError:
        print("Can't test GET /calendar/{}/{{id}} with valid ID. "
              "No entries found".format(result))

    # Test GET /{result}/{id} with invalid ID
    invalid_result = utils.get_result_by_id(result, "-1")
    validate_response(self, invalid_result, 404)


if __name__ == "__main__":
    namespace, args = utils.parse_args()
    config = json.load(open(namespace.inputfile))
    utils.set_url(config)
    utils.post_token(config)
    sys.argv = args
    event_body = config["valid_event_body"]
    unittest.main()
