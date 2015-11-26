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
        -Xmx16M -Xms16M -Xmn8M -XX:MaxPermSize=30M -XX:MaxMetaspaceSize=64M -XX:+HeapDumpOnOutOfMemoryError \
        jframe.launcher.Main >/dev/null 2>&1 &
    echo "Application start finished!"
}

stop()
{
    PID_PATH=${APP_HOME}/temp/app.pid
    if [ -f "${PID_PATH}" ]; then
	    PID=`cat ${PID_PATH}`
	    kill -TERM ${PID}
	    rm -rf ${PID_PATH}
	    echo "Application shutdown finished!"
    else
	    echo "Not found app.pid file!"
    fi

    PID_PATH=${APP_HOME}/temp/daemon.pid
    if [ -f "${PID_PATH}" ]; then
	    PID=`cat ${PID_PATH}`
	    kill -TERM ${PID}
	    rm -rf ${PID_PATH}
	    echo "Daemon process shutdown finished!"
    fi
}

main()
{
	java -Dapp.home=${APP_HOME} \
        -Dlogback.configurationFile=${APP_HOME}/conf/logback-plugin.xml \
        -cp ${APP_HOME}/lib/*:${APP_HOME}/plugin/*:$PATH \
        -Xmx60M -Xms10M -XX:MaxPermSize=50M -XX:+HeapDumpOnOutOfMemoryError \
        $1
}

[ $# -gt 0 ] || usage

APP_HOME=`dirname $(cd "$(dirname "$0")"; pwd)`

if [ $1 = "start" ]; then
    start
elif [ $1 = "stop" ]; then 
    stop
elif [ $1 = "-m" ]; then
	main $2
else
    usage
fi

