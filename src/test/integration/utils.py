import argparse
import requests
import json
import sys


def set_url(config):
    global url
    url = config["hostname"] + config["version"] + config["api"]


def set_headers(token):
    global headers
    headers = {"Authorization": "Bearer {}".format(token)}


def parse_args():
    parser = argparse.ArgumentParser()
    parser.add_argument("-i", help="path to input file", dest="inputfile")
    namespace, args = parser.parse_known_args()
    return namespace, sys.argv[:1] + args


def post_token(config):
    data = {
        "client_id": config["client_id"],
        "client_secret": config["client_secret"],
        "grant_type": "client_credentials"
    }
    response = requests.post(url=config["token_api_url"], data=data)
    if "access_token" not in response.json():
        sys.exit("Error: invalid OAUTH2 credentials")
    set_headers(response.json()["access_token"])


def get_events():
    return requests.get(
        url="{}events".format(url), headers=headers
    )


def post_event(body):
    return requests.post(
        url="{}events".format(url), headers=headers, json=body
    )


def get_event_by_id(id):
    return requests.get(
        url="{url}events/{id}".format(url=url, id=id), headers=headers
    )


def put_event(id, body):
    return requests.put(
        url="{url}events/{id}".format(url=url, id=id),
        headers=headers,
        json=body
    )


def delete_event(id):
    return requests.delete(
        url="{url}events/{id}".format(url=url, id=id), headers=headers
    )
