import utils
import unittest
import json
import sys


class TestStringMethods(unittest.TestCase):

    def test_events_combined(self):
        # POST with server-generated ID
        server_id = utils.post_event(config["valid_event_create_body"])
        validate_response(self, server_id, 202, "event")

        valid_id = server_id.json()["data"]["id"]

        # GET {id} with valid ID
        valid_get = utils.get_event_by_id(valid_id)
        validate_response(self, valid_get, 200, "event")

        # PUT with valid ID
        valid_put = utils.put_event(
            valid_id,
            config["valid_event_update_body"]
        )
        validate_response(self, valid_put, 200, "event")

        # DELETE with valid ID
        valid_delete = utils.delete_event(valid_id)
        validate_response(self, valid_delete, 204)

    def test_post_event(self):
        # Client-generated ID
        config["valid_event_create_body"]["data"]["id"] = (
            config["unused_event_id"]
        )
        client_id = utils.post_event(config["valid_event_create_body"])
        validate_response(self, client_id, 202, "event")
        self.assertEqual(
            client_id.json()["data"]["id"],
            config["unused_event_id"]
        )

        # Clean up event
        utils.delete_event(config["unused_event_id"])

    def test_put_event(self):
        # Invalid ID
        config["valid_event_update_body"]["data"]["id"] = (
            config["invalid_event_id"]
        )
        invalid_id = utils.put_event(
            config["valid_event_update_body"]["data"]["id"],
            config["valid_event_update_body"]
        )
        validate_response(self, invalid_id, 404)


def validate_response(self, response, code=None, res_type=None, message=None):
    if code:
        self.assertEqual(response.status_code, code)
    if res_type:
        self.assertEqual(response.json()["data"]["type"], res_type)
    if message:
        self.assertIn(message, response.json()["developerMessage"])


if __name__ == "main":
    namespace, args = utils.parse_args()
    config = json.load(open(namespace.inputfile))
    utils.set_url(config)
    utils.post_token(config)
    sys.arg = args
    unittest.main()
