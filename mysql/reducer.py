#!/usr/bin/env python
import sys
import json

for line in sys.stdin:
    splited = line.split('\t')
    time = splited[0]
    other = json.loads(splited[1])

    output = [other['tweet_id'], other['censored_text'], str(other['score']), time, other['user_id']]
    sys.stdout.write('\x01'.join(output).encode('utf-8'))
    sys.stdout.write('\x02')
    sys.stdout.flush()