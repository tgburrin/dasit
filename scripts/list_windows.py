#!/usr/bin/env python3

import sys
import argparse

from common import list_windows

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

    args = parser.parse_args()
    args_dict = vars(args)
    return args_dict

## Main
args = parse_arguments()
for dsw in list_windows(args["dataset"]):
    print(f"'{dsw['datasetName']}': {dsw['windowStartDateTime']} -> {dsw['windowEndDateTime']}")

sys.exit(0)
