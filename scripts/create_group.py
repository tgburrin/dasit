#!/usr/bin/env python3

import sys
import argparse

from common import create_group

def parse_arguments():
    parser = argparse.ArgumentParser(
        description=("A script for creating groups in DASIT")
    )

    parser.add_argument(
        "-g",
        "--group",
        dest="group",
        required=True,
        type=str,
        help="The unique group name"
    )

    parser.add_argument(
        "-e",
        "--email",
        dest="email",
        required=True,
        type=str,
        help="The email address for the group"
    )

    args = parser.parse_args()
    args_dict = vars(args)
    return args_dict

## Main
args = parse_arguments()

g = create_group(args['group'], args['email'])
if g:
    print(f"'{g['name']}' : '{g['emailAddress']}'")
    sys.exit(0)
else:
    sys.exit(1)
