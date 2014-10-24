from django.shortcuts import render
from django.http import HttpResponse
from datetime import datetime

def handle_request(request):
    if not 'key' in request.GET or not request.GET['key']:
       return HttpResponse("")

    Y = int(request.GET['key']) / 6876766832351765396496377534476050002970857483815262918450355869850085167053394672634315391224052153

    response = "%d\nccteam, 6028-0384-4018, 4756-0270-5080, 1894-1779-2072\n%s\n" % (Y, datetime.now().strftime('%Y-%m-%d+%H:%M:%S'))

    return HttpResponse(response)