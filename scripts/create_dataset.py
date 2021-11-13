#!/usr/bin/env python3

import sys
import argparse

from common import create_dataset

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
        "-g",
        "--group",
        dest="group",
        required=True,
        type=str,
        help="The group name for the command"
    )

    args = parser.parse_args()
    args_dict = vars(args)
    return args_dict

## Main

args = parse_arguments()
ds = create_dataset(args['dataset'], args['group'])
if ds:
    print(f"'{ds['group']['name']}' : '{ds['name']}' : {ds['status']}")
    sys.exit(0)
else:
    sys.exit(1)
