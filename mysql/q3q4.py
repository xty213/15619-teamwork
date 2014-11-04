import ujson
import sys
import re
import hashlib
from datetime import datetime

reload(sys)
sys.setdefaultencoding('utf8')

q3 = open(sys.argv[2], 'w')
q4 = open(sys.argv[3], 'w')

mod = 2 ** 63

with open(sys.argv[1]) as f:
    for line in f:
        if not line == '\n':
            obj = ujson.loads(line)
            if 'retweeted_status' in obj:
                original_user_id = obj['retweeted_status']['user']['id_str']
                retweet_user_id = obj['user']['id_str']
                q3.write('%s\t%s\n' % (original_user_id, retweet_user_id))

            if obj['entities']['hashtags']:
                location = None
                if obj['place'] and obj['place']['name']:
                    location = obj['place']['name']
                elif obj['user']['time_zone'] and not re.findall(r'\btime\b', obj['user']['time_zone'].lower()):
                    location = obj['user']['time_zone']
                else:
                    continue

                hashed_location = int(hashlib.md5(location).hexdigest(), base=16) % mod
                date = datetime.strptime(obj['created_at'], '%a %b %d %H:%M:%S +0000 %Y').strftime('%Y%m%d')
                tweet_id = obj['id_str']
                for hashtag in obj['entities']['hashtags']:
                    q4.write('%s\t%d\t%s\t%s\t%s\t%d\n' % (tweet_id, hashed_location, location, date, hashtag['text'], hashtag['indices'][0]))

q3.close()
q4.close()
