import falcon
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

app = falcon.API()
q1 = Q1Resource()
app.add_route('/q1', q1)
