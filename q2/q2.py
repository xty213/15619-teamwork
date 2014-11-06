import falcon
import pymysql
from datetime import datetime

class Q2Resource:
    def __init__(self):
        self.db = pymysql.connect(host='ec2-54-172-45-157.compute-1.amazonaws.com',
                                  user = 'ccteam',
                                  passwd = 'password',
                                  db = '15619project',
                                  charset='utf8')
        self.cur = self.db.cursor()
        self.cur.execute('SET NAMES utf8mb4')
        self.info = 'ccteam,6028-0384-4018,4756-0270-5080,1894-1779-2072'

    def __del__(self):
        self.db.close()


    def on_get(self, req, resp):
        user_id = req.get_param('userid')
        tweet_time = datetime.strptime(req.get_param('tweet_time'), '%Y-%m-%d %H:%M:%S')

        timestamp = int((tweet_time - datetime(1970, 1, 1)).total_seconds())

        self.cur.execute('SELECT * FROM q2 WHERE user_id=%s AND timestamp=%s', (user_id, str(timestamp)))
        result = sorted(self.cur.fetchall(), key=lambda x: x[0])
        result = ['%s:%s:%s' % (str(x[0]), str(x[4]), x[5]) for x in result]

        resp.status = falcon.HTTP_200
        resp.body = '%s\n%s\n' % (self.info,
                                  "\n".join(result))

app = falcon.API()
q2 = Q2Resource()
app.add_route('/q2', q2)
