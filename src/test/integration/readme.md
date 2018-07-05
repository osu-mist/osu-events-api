# OSU Events API Integration Tests

## Usage

1. Install dependencies:
```
$ pip3 install -r requirements.txt
```
2. Copy configuration-example.json to configuration.json and modify as necessary.
3. Run the integration tests:
```
$ python3 integration-tests.py -i configuration.json [--info] [-v]
```
Use `--info` to view `INFO` logging.

## Docker Method
```bash
$ docker build --rm -t osu-events-api-integration-tests .
$ docker run \
-v "$PWD"/configuration.json:/usr/src/app/configuration.json:ro \
osu-events-api-integration-tests [--info] [-v]
```
