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


# Retrieves OAUTH2 access token and sets authorization in headers
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


def post_event(body):
    return requests.post(
        url="{}events".format(url), headers=headers, json=body
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


# Generic request for GET /calendar/{result}
def get_results(result):
    return requests.get(
        url="{url}{result}".format(url=url, result=result),
        headers=headers
    )


# Generic request for GET /calendar/{result}/{id}
def get_result_by_id(result, id):
    return requests.get(
        url="{url}{result}/{id}".format(
            url=url, result=result, id=id
        ),
        headers=headers
    )
