#!/usr/bin/env python

import ujson
import sys
import re
import hashlib
import happybase
from datetime import datetime

reload(sys)
sys.setdefaultencoding('utf8')

q3_table = happybase.Connection("ec2-54-173-99-121.compute-1.amazonaws.com").table('q3')
q4_table = happybase.Connection("ec2-54-173-99-121.compute-1.amazonaws.com").table('q4')

q3_counter = 0
q3_batch = q3_table.batch()

for line in sys.stdin:
    if line != '\n':
        obj = ujson.loads(line)

        # q3
        if 'retweeted_status' in obj:
            original_user_id = obj['retweeted_status']['user']['id_str']
            retweet_user_id = obj['user']['id_str']

            q3_counter += 1
            q3_batch.put(original_user_id, {"by:" + retweet_user_id: '1'})
            
            if q3_counter > 200:
                q3_batch.send()
                q3_counter = 0

            
        # q4
        if obj['entities']['hashtags']:
            location = None
            if obj['place'] and obj['place']['name']:
                location = obj['place']['name']
            elif obj['user']['time_zone'] and not re.findall(r'\btime\b', obj['user']['time_zone'].lower()):
                location = obj['user']['time_zone']
            else:
                continue

            date = datetime.strptime(obj['created_at'], '%a %b %d %H:%M:%S +0000 %Y').strftime('%Y-%m-%d')
            tweet_id = obj['id_str']
            row_key = date + location
            for hashtag in obj['entities']['hashtags']:
                tag_col = ('tag:' + hashtag['text']).encode('utf8')
                tag_dict = q4_table.row(row_key, columns=(tag_col,))
                if len(tag_dict) == 0:
                    q4_table.put(row_key, {tag_col: "%s:%s" % (tweet_id, hashtag['indices'][0])})
                else:
                    q4_table.put(row_key, {tag_col: "%s,%s:%s" % (tag_dict[tag_col], tweet_id, hashtag['indices'][0])})

q3_batch.send()

print "OK"