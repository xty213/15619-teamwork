#!/usr/bin/env python

import ujson
import sys
import re
import hashlib
import happybase
from datetime import datetime

reload(sys)
sys.setdefaultencoding('utf8')

q3_table = happybase.Connection("ec2-54-85-195-109.compute-1.amazonaws.com").table('q3')
q4_table = happybase.Connection("ec2-54-85-195-109.compute-1.amazonaws.com").table('q4')

for line in sys.stdin:
    if line != '\n':
        obj = ujson.loads(line)

        # q3
        if 'retweeted_status' in obj:
            original_user_id = obj['retweeted_status']['user']['id_str']
            retweet_user_id = obj['user']['id_str']

            retweet_col = "by:" + retweet_user_id
            original_col = "by:" + original_user_id

            original_was_retweeted = len(q3_table.row(original_user_id, columns=(retweet_col,)))
            if not original_was_retweeted:
                rewteet_was_retweeted = len(q3_table.row(retweet_user_id, columns=(original_col,)))
                if rewteet_was_retweeted:
                    q3_table.put(original_user_id, {retweet_col: '1'})
                    q3_table.put(retweet_user_id, {original_col: '1'})
                else:
                    q3_table.put(original_user_id, {retweet_col: '0'})
            
        # q4
        if obj['entities']['hashtags']:
            location = None
            if obj['place'] and obj['place']['name']:
                location = obj['place']['name']
            elif obj['user']['time_zone'] and not re.findall(r'\btime\b', obj['user']['time_zone'].lower()):
                location = obj['user']['time_zone']
            else:
                continue

            date = datetime.strptime(obj['created_at'], '%a %b %d %H:%M:%S +0000 %Y').strftime('%Y%m%d')
            tweet_id = obj['id_str']
            row_key = date + location
            for hashtag in obj['entities']['hashtags']:
                tag_col = ('tag:' + hashtag['text']).encode('utf8')
                tag_dict = q4_table.row(row_key, columns=(tag_col,))
                if len(tag_dict) == 0:
                    q4_table.put(row_key, {tag_col: "%s:%s" % (tweet_id, hashtag['indices'][0])})
                else:
                    q4_table.put(row_key, {tag_col: "%s,%s:%s" % (tag_dict[tag_col], tweet_id, hashtag['indices'][0])})

print "OK"