#!/bin/bash

rm -f ./tlc200bot.pid
java -server \
    -cp ./ \
    -jar tlc200bot.jar &
echo $! > ./tlc200bot.pid