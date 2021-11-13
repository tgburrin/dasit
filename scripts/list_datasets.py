#!/usr/bin/env python3

import sys

from common import list_datasets

for ds in list_datasets():
    print(f"'{ds['group']['name']}' : '{ds['name']}' : {ds['status']}")

sys.exit(0)
