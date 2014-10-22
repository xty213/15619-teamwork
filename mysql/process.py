# -*- coding: utf-8 -*-
import MySQLdb
import sys
import ujson
from datetime import datetime
from StringIO import StringIO
import traceback

def decipher(string):
    letters = 'abcdefghijklmnopqrstuvwxyz'
    sb = []
    for char in string:
        if 'a' <= char <= 'z':
            sb.append(letters[(ord(char) - ord('a') + 13) % 26])
        else:
            sb.append(char)
    return "".join(sb)



filename = sys.argv[1]
host = sys.argv[2]
username = sys.argv[3]
password = sys.argv[4]

# sentiment score dict
afinn = {}
with open('AFINN.json') as f:
    line = f.readline()
    afinn = ujson.loads(line)

# banned words
banned = set()
with open('banned.txt') as f:
    for line in f:
        banned.add(decipher(line.strip()))


# connect to mysql
db = MySQLdb.connect(host=host,
                     user = username,
                     passwd = password,
                     db = '15619project',
                     charset='utf8',
                     init_command='SET NAMES utf8mb4')

cur = db.cursor()

with open(filename) as f:
    for line in f:
        if line == '\n':
            continue
        tweet = ujson.loads(line)

        tweet_id = tweet['id_str']

        user_id = tweet['user']['id_str']

        # time
        origin_time = datetime.strptime(tweet['created_at'], '%a %b %d %H:%M:%S +0000 %Y')
        fixed_time = origin_time.strftime('%Y-%m-%d+%H:%M:%S')

        origin_text = tweet['text']
        words = []
        p, q = 0, 0
        while q < len(origin_text):
            if '0' <= origin_text[q] <= '9' or 'a' <= origin_text[q] <= 'z' or 'A' <= origin_text[q] <= 'Z':
                if '0' <= origin_text[p] <= '9' or 'a' <= origin_text[p] <= 'z' or 'A' <= origin_text[p] <= 'Z':
                    q += 1
                else:
                    words.append(origin_text[p:q])
                    p = q
            else:
                if not ('0' <= origin_text[p] <= '9' or 'a' <= origin_text[p] <= 'z' or 'A' <= origin_text[p] <= 'Z'):
                    q += 1
                else:
                    words.append(origin_text[p:q])
                    p = q
        words.append(origin_text[p:q])
        censored_sb = []
        score = 0
        for word in words:
            if len(word) == 0:
                continue
            if '0' <= word[0] <= '9' or 'a' <= word[0] <= 'z' or 'A' <= word[0] <= 'Z':
                lower = word.lower()
                if lower in afinn:
                    score += afinn[lower]
                if lower in banned:
                    word_append = word[0] + '*' * (len(word) - 2) + word[-1:]
                else:
                    word_append = word
                censored_sb.append(word_append)
            else:
                censored_sb.append(word)
            censored_text = "".join(censored_sb)

        try:
            cur.execute('INSERT IGNORE INTO tweets VALUES(%s, %s, %s, %s, %s)', (tweet_id, censored_text, str(score), fixed_time, user_id))
        except:
            db.rollback()
            traceback.print_exc()
            print 'Rolling back...'
            break

db.commit()
db.close()







