#!/bin/python
import os

files = []
with open('files.txt') as f:
    for line in f:
        files.append(line.strip())

for file in files:
    file_name_gz = file.split('/')[-1]
    file_name = file_name_gz.split('.')[0]
    print file_name

    os.system('s3cmd get %s' % file)
    os.system('gzip -d %s' % file_name_gz)
    os.system('java phase1.Process %s > %s' % (file_name, file_name + '_processed'))
    os.system('rm %s' % file_name)
    os.system('s3cmd put %s %s' % (file_name + '_processed', 's3://hquan/15619Project/MySQL/'))
    os.system('rm %s' % (file_name + '_processed',))
