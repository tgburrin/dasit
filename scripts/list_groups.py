#!/usr/bin/env python3

import sys
from common import list_groups

for g in list_groups():
    print(f"'{g['name']}' : {g['status']}")

sys.exit(0)
