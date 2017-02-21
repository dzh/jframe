#! /bin/sh

PID_PATH=/data/es/es.pid

usage()
{
    echo "Usage: ${0##*/}  {start|stop} "
    exit 1
}

start()
{
    if [ -f "${PID_PATH}" ]; then
        echo "Application has started!"
        exit 0
    fi

    bin/elasticsearch -d -p ${PID_PATH}
    echo "Application start finished!"
}

stop()
{
    if [ -f "${PID_PATH}" ]; then
        PID=`cat ${PID_PATH}`
        kill ${PID}
        rm -rf ${PID_PATH}
        echo "Application shutdown finished!"
    else
        echo "Not found app.pid file!"
    fi
}

if [ $1 = "start" ]; then
    start
elif [ $1 = "stop" ]; then 
    stop
else
    usage
fi