#!/bin/bash
if [ $# -lt 1 ]; then
	cat<<HELP
USAGE: shell command [option]
command:
	clean
	debug
	test
	online

HELP
	exit 0
fi


CMD="$1"

if [[ "$CMD" = "clean" ]]; then
	echo "start to clean files ..."
	rm -rf ./diana-web/src/main/resources/static
	rm -rf ./diana-web/src/main/resources/templates
	exit 0
fi

if [[ "$CMD" = "debug" ]]; then
	echo "start to compile files for debug ..."
	# rm -rf ./diana-web/src/main/resources/static
	# rm -rf ./diana-web/src/main/resources/templates
	fis3 release -d ./diana-web/src/main/resources
	exit 0
fi

if [[ "$CMD" = "test" ]]; then
	VER=`git log -n 1 --date=short --format=format:"%cd%h"`
	echo "start to compile files for test ... $VER"
	rm -rf ./diana-web/src/main/resources/static
	rm -rf ./diana-web/src/main/resources/templates
	fis3 release test -c -d ./diana-web/src/main/resources
	mv diana-web/src/main/resources/static ./$VER
	scp -r $VER/* root@172.16.200.190:/app/nginx/html/diana/
	rm -rf $VER
	exit 0
fi

if [[ "$CMD" = "online" ]]; then
	VER=`git log -n 1 --date=short --format=format:"%cd%h"`
	echo "start to compile files for online ... $VER"
	rm -rf ./diana-web/src/main/resources/static
	rm -rf ./diana-web/src/main/resources/templates
	rm -rf *.tar
	fis3 release online -c -d ./diana-web/src/main/resources --child-flag=$VER
	mv diana-web/src/main/resources/static ./$VER
	tar -zcf diana-web-static.tar $VER
	rm -rf $VER
	exit 0
fi

echo "-shell: command not found: $CMD"