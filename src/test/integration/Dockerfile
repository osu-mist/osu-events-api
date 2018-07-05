FROM python:3

WORKDIR /usr/src/app

COPY . .
RUN pip3 install -r requirements.txt

USER nobody:nogroup

ENTRYPOINT [ "python3", "integration-tests.py", "-i", "configuration.json", "valid_event_body.json" ]
