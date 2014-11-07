import falcon
import happybase
from datetime import datetime

class Q1Resource:
    def __init__(self):
        self.X = 6876766832351765396496377534476050002970857483815262918450355869850085167053394672634315391224052153L
        self.info = 'ccteam,6028-0384-4018,4756-0270-5080,1894-1779-2072'

    def on_get(self, req, resp):
        XY = int(req.get_param('key'))
        Y = XY / self.X

        resp.status = falcon.HTTP_200
        resp.body = '%d\n%s\n%s\n' % (Y,
                                      self.info,
                                      datetime.now().strftime('%Y-%m-%d %H:%M:%S'))


class Q2Resource:
    def __init__(self):
        self.DNS = 'ec2-54-173-71-224.compute-1.amazonaws.com'
        self.table = 'tweets'

    def on_get(self, req, resp):
        if not req.get_param('userid') or not req.get_param('tweet_time'):
            resp.status = falcon.HTTP_200
            resp.body = ''
            return

        q2_table = happybase.Connection(self.DNS).table(self.table)
        row = q2_table.row('%s %s' % (req.get_param('userid'), req.get_param('tweet_time').replace(' ', '+')))

        resp.status = falcon.HTTP_200
        resp.body = 'ccteam,6028-0384-4018,4756-0270-5080,1894-1779-2072\n%s:%s:%s\n' % (row['attr:tweet_id'], row['attr:score'], row['attr:censored_text'])


class Q3Resource:
    def __init__(self):
        self.DNS = 'ec2-54-173-71-224.compute-1.amazonaws.com'
        self.table = 'q3'

    def on_get(self, req, resp):
        if not req.get_param('userid'):
            resp.status = falcon.HTTP_200
            resp.body = ''
            return

        q3_table = happybase.Connection(self.DNS).table(self.table)
        row = q3_table.row(req.get_param('userid'))

        sorted_row = sorted([k[3:] for k,v in row.items()], key=lambda i:int(i))
        resp_str = 'ccteam,6028-0384-4018,4756-0270-5080,1894-1779-2072\n'
        for uid in sorted_row:
            if q3_table.row(uid, columns=['by:' + req.get_param('userid')]):
                resp_str += '(%s)\n' % uid
            else:
                resp_str += '%s\n' % uid

        resp.status = falcon.HTTP_200
        resp.body = resp_str


class Q4Resource:
    def __init__(self):
        self.DNS = 'ec2-54-173-71-224.compute-1.amazonaws.com'
        self.table = 'q4'

    def on_get(self, req, resp):
        if not req.get_param('date') or not req.get_param('location') or \
           not req.get_param('m') or not req.get_param('n'):
            resp.status = falcon.HTTP_200
            resp.body = ''
            return

        q4_table = happybase.Connection(self.DNS).table(self.table)
        row = q4_table.row(req.get_param('date') + req.get_param('location'))

        splited_row = [(k[4:],reduce(lambda x,y:x if y[0] in [p[0] for p in x] else x + [y], [[], ] + sorted([s.split(':') for s in v.split(',')], key=lambda t:int(t[0])))) for k,v in row.items()]
        sorted_row = sorted(splited_row, key=lambda t:(-len(t[1]), int(t[1][0][0]), int(t[1][0][1])))
        m = int(req.get_param('m'))
        n = int(req.get_param('n'))
        resp_str = 'ccteam,6028-0384-4018,4756-0270-5080,1894-1779-2072\n'
        for tag,tweet_list in sorted_row[m-1:n]:
            resp_str += '%s:%s\n' % (tag, ','.join([t[0] for t in tweet_list]))

        resp.status = falcon.HTTP_200
        resp.body = resp_str


app = falcon.API()
q1 = Q1Resource()
app.add_route('/q1', q1)
q2 = Q2Resource()
app.add_route('/q2', q2)
q3 = Q3Resource()
app.add_route('/q3', q3)
q4 = Q4Resource()
app.add_route('/q4', q4)