from django.shortcuts import render
from django.http import HttpResponse
import happybase

def handle_request(request):
    conn = happybase.Connection('ec2-54-173-66-253.compute-1.amazonaws.com')
    tweets = conn.table('tweets')

    if not 'userid' in request.GET or not request.GET['userid'] or \
       not 'tweet_time' in request.GET or not request.GET['tweet_time']:
       return HttpResponse("")

    userid = request.GET['userid']
    time = request.GET['tweet_time'].replace(' ', '+')

    data = tweets.row('%s %s' % (userid, time))

    # assume we will surely find at least one record if a userid and a time is given
    response = "ccteam, 6028-0384-4018, 4756-0270-5080, 1894-1779-2072\n%s:%s:%s\n" % (data['attr:tweet_id'], data['attr:score'], data['attr:censored_text'])

    return HttpResponse(response)