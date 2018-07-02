import utils
import unittest
import json
import sys


class TestStringMethods(unittest.TestCase):

    # Test POST, GET, PUT, and DELETE
    def test_events_combined(self):
        # Test POST /calendar/events with valid body
        server_id = utils.post_event(valid_event_body)
        validate_response(self, server_id, 202, "events")

        valid_id = server_id.json()["data"]["id"]

        # Test GET /calendar/events/{id} with valid ID
        valid_get = utils.get_event_by_id(valid_id)
        validate_response(self, valid_get, 200, "events")

        # Test PUT /calendar/events/{id} with valid ID and body
        valid_put = utils.put_event(valid_id, valid_event_body)
        validate_response(self, valid_put, 200, "events")

        # Test DELETE /calendar/events/{id} with valid ID
        valid_delete = utils.delete_event(valid_id)
        validate_response(self, valid_delete, 204)

    # Test GET /calendar/events
    def test_get_events(self):
        valid_get = utils.get_events()
        validate_response(self, valid_get, 200)

    # Test POST /calendar/events
    def test_post_event(self):
        # Invalid date in instance
        good_date = (valid_event_body["data"]["attributes"]
                     ["instances"][0]["start"])
        valid_event_body["data"]["attributes"]["instances"][0]["start"] = (
            "badDate"
        )
        bad_date = utils.post_event(valid_event_body)
        validate_response(self, bad_date, 400, message="Could not parse job")
        valid_event_body["data"]["attributes"]["instances"][0]["start"] = (
            good_date
        )

        # Invalid campusID
        valid_event_body["data"]["attributes"]["campusID"] = "badCampus"
        bad_campus = utils.post_event(valid_event_body)
        validate_response(self, bad_campus, 400, message="campusID")
        valid_event_body["data"]["attributes"]["campusID"] = None

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
        valid_event_body["data"]["id"] = (
            config["invalid_event_id"]
        )
        invalid_id = utils.put_event(
            valid_event_body["data"]["id"], valid_event_body
        )
        validate_response(self, invalid_id, 404)
        valid_event_body["data"]["id"] = None

        # Create test event
        valid_event = utils.post_event(valid_event_body)
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


def validate_response(self, response, code=None, res_type=None, message=None):
    if code:
        self.assertEqual(response.status_code, code)
    if res_type:
        self.assertEqual(response.json()["data"]["type"], res_type)
    if message:
        self.assertIn(message, response.json()[0]["developerMessage"])


def validate_bad_list(self, list_name, put_id=None):
    valid_event_body["data"]["attributes"][list_name] = []
    valid_event_body["data"]["attributes"][list_name].append("badItem")
    if put_id:
        bad_response = utils.put_event(put_id, valid_event_body)
    else:
        bad_response = utils.post_event(valid_event_body)
    validate_response(self, bad_response, 400, message=list_name)
    valid_event_body["data"]["attributes"][list_name] = None


if __name__ == "__main__":
    namespace, args = utils.parse_args()
    config = json.load(open(namespace.inputfile))
    utils.set_url(config)
    utils.post_token(config)
    sys.argv = args
    valid_event_body = config["valid_event_body"]
    unittest.main()
