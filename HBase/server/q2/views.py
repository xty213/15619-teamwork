from django.shortcuts import render
from django.http import HttpResponse
import happybase

def handle_request(request):
    conn = happybase.Connection('ec2-54-173-32-173.compute-1.amazonaws.com')
    tweets = conn.table('tweets')

    if not 'userid' in request.GET or not request.GET['userid'] or \
       not 'tweet_time' in request.GET or not request.GET['tweet_time']:
       return HttpResponse("")

    userid = request.GET['userid']
    time = request.GET['tweet_time']

    result = tweets.scan(filter="SingleColumnValueFilter('attr','time',=,'regexstring:^%s$') AND SingleColumnValueFilter('attr','user_id',=,'regexstring:^%s$')" % (time, userid))

    response = "ccteam, 6028-0384-4018, 4756-0270-5080, 1894-1779-2072\n"

    for key, data in result:
        response += "%s:%s:%s" % (data['attr:tweet_id'], data['attr:score'], data['attr:censored_text'])

    return HttpResponse(response + "\n")