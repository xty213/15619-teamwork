#!/bin/bash

sudo apt-get update
sudo apt-get -q -y install build-essential python-dev
wget https://bootstrap.pypa.io/get-pip.py
sudo python get-pip.py
sudo pip install ujson
sudo apt-get -q -y install python2.7-mysqldb
