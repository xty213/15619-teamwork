from django.shortcuts import render
from django.http import HttpResponse
import happybase

def handle_request(request):
    conn = happybase.Connection('ec2-54-173-32-173.compute-1.amazonaws.com')
    students = conn.table('student')

    if not 'first' in request.GET or not request.GET['first'] or \
       not 'last' in request.GET or not request.GET['last']:
       return HttpResponse("")

    first = request.GET['first']
    last = request.GET['last']

    result = students.scan(filter="SingleColumnValueFilter('name','first',=,'regexstring:^%s$') AND SingleColumnValueFilter('name','last',=,'regexstring:^%s$')" % (first, last))

    response = "ccteam, 6028-0384-4018, 4756-0270-5080, 1894-1779-2072\n"

    for key, data in result:
        response += "%s:%s:%s" % (key, data['name:first'], data['name:last'])

    return HttpResponse(response + "\n")