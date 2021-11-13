#!/usr/bin/env python3

import sys
import re
import argparse

from common import check_window

def match_iso_dt(arg, exp=re.compile(r'^20[0-9]{2}-[01][0-9]-[0-3][0-9]T[0-2][0-9]:[0-5][0-9]:[0-6][0-9]Z$')):
    if not exp.match(arg):
        raise argparse.ArgumentTypeError
    return arg

def parse_arguments():
    parser = argparse.ArgumentParser(
        description=("A script for interacting with DASIT")
    )

    parser.add_argument(
        "-d",
        "--dataset",
        dest="dataset",
        required=True,
        type=str,
        help="The dataset name for the command"
    )

    parser.add_argument(
        "-s",
        "--start",
        dest="start_dt",
        required=True,
        type=match_iso_dt,
        help="The window start time"
    )

    parser.add_argument(
        "-e",
        "--end",
        dest="end_dt",
        required=True,
        type=match_iso_dt,
        help="The window end time"
    )

    args = parser.parse_args()
    args_dict = vars(args)
    return args_dict

## Main

args = parse_arguments()
pw = check_window(args['dataset'], args['start_dt'], args['end_dt'])
if pw.keys():
    print(f'{pw["windowStartDateTime"]} -> {pw["windowEndDateTime"]}')
    sys.exit(0)
else:
    sys.exit(1)
