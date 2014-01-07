#! /bin/sh

usage()
{
    echo "Usage: ${0##*/}  {start|stop} "
    exit 1
}

start()
{
    PID_PATH=${APP_HOME}/temp/daemon.pid
    if [ -f "${PID_PATH}" ]; then
        echo "Application has started!"
        exit 0
    fi

    nohup java -Dapp.home=${APP_HOME} \
        -Dlogback.configurationFile=${APP_HOME}/conf/logback-daemon.xml \
        -cp ${APP_HOME}/lib/*:$PATH \
        jframe.launcher.Main >/dev/null 2>&1 &
    echo "Application start finished!"
}

stop()
{
    PID_PATH=${APP_HOME}/temp/app.pid
    if [ -f "${PID_PATH}" ]; then
	    PID=`cat ${PID_PATH}`
	    kill -TERM ${PID}
	    echo "Application shutdown finished!"
    else
	    echo "Not found app.pid file!"
    fi

    PID_PATH=${APP_HOME}/temp/daemon.pid
    if [ -f "${PID_PATH}" ]; then
	    PID=`cat ${PID_PATH}`
	    kill -TERM ${PID}
	    echo "Daemon process shutdown finished!"
    fi
}

[ $# -gt 0 ] || usage

APP_HOME=`dirname $(cd "$(dirname "$0")"; pwd)`

if [ $1 = "start" ]; then
    start
elif [ $1 = "stop" ]; then 
    stop
else
    usage
fi

