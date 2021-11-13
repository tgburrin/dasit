#!/usr/bin/env python3

import http.client
import urllib.parse
import base64
import json
import ssl
import sys

DASIT_HOST="localhost:8443"

def make_read_req(endpoint):
    payload = None

    ssl_ctx = ssl._create_unverified_context()
    cli = http.client.HTTPSConnection(DASIT_HOST, context=ssl_ctx)
    cli.request('GET', urllib.parse.quote(endpoint))
    resp = cli.getresponse()
    payload = resp.read()

    if resp.status != 200:
        raise ValueError(payload.decode())

    cli.close()
    return json.loads(payload)

def make_write_req(endpoint, payload):
    headers = {"Content-Type": "application/json"}
    ssl_ctx = ssl._create_unverified_context()
    cli = http.client.HTTPSConnection(DASIT_HOST, context=ssl_ctx)

    cli.request('POST', urllib.parse.quote(endpoint), headers=headers, body=json.dumps(payload))
    resp = cli.getresponse()

    if resp.status not in [200, 201, 204]:
        raise ValueError(resp.read().decode())
    elif resp.status == 204:
        payload = '{}'
    else:
        payload = resp.read()

    cli.close()
    return json.loads(payload)

def stderr_logging(fn):
    def logging_wrapper(*args, **kwargs):
        #print(f'{fn} {args} {kwargs}')
        try:
            return fn(*args, **kwargs)
        except Exception as e:
            try:
                err = json.loads(str(e))
                print(f"status: {err['status']}", file=sys.stderr)
                print(f"error: {err['error']}", file=sys.stderr)
                print(f"message:\n===\n{err['message']}\n===", file=sys.stderr)
            except:
                print(f'error: {str(e)}', file=sys.stderr)

        return dict()
    return logging_wrapper

@stderr_logging
def create_group(group, email_address):
    req = { "emailAddress": email_address, "name": group, "status": None }
    return make_write_req("/groups/create", req)

@stderr_logging
def create_dataset(dataset, group):
    req = { "datasetName": dataset, "ownerGroupName": group }
    return make_write_req("/dataset_registration/create", req)

@stderr_logging
def publish_window(dataset, start_dt, end_dt):
    req = {"datasetName": dataset, "windowStartDateTime": start_dt, "windowEndDateTime": end_dt}
    return make_write_req("/dataset_publish/publish_window", req)

@stderr_logging
def unpublish_window(dataset, start_dt, end_dt):
    req = {"datasetName": dataset, "windowStartDateTime": start_dt, "windowEndDateTime": end_dt}
    return make_write_req("/dataset_publish/remove_window", req)

@stderr_logging
def check_window(dataset, start_dt, end_dt):
    req = {"datasetName": dataset, "windowStartDateTime": start_dt, "windowEndDateTime": end_dt}
    return make_write_req("/dataset_publish/check_window", req)

@stderr_logging
def list_datasets():
    return [ds for ds in make_read_req("/dataset_registration/list")]

@stderr_logging
def list_groups():
    return [g for g in make_read_req("/groups/list")]

@stderr_logging
def list_windows(dataset):
    return [w for w in make_read_req(f'/dataset_publish/list_windows/{dataset}')]
