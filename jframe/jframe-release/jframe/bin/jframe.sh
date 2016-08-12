#! /bin/sh
## TODO ##############################################################
# more shell options: debug help 
# shell options std parser
# check jvm version
# file.config
######################################################################

## VM Arguments ##############################################################
# APP_HOME 
# MAIN_LAUNCHER
# JVM_OPT
# JVM_OPT_DEBUG
######################################################################

[ $# -gt 0 ] || usage

APP_HOME=`dirname $(cd "$(dirname "$0")"; pwd)`

MAIN_LAUNCHER="jframe.launcher.MainLauncher"

JVM_OPT="-Xmx60M -Xms60M -Xmn50M -XX:MaxPermSize=60M -XX:+HeapDumpOnOutOfMemoryError"
JVM_OPT_DEBUG="-Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=7000,server=y,suspend=n"

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
        -Dlauncher=${MAIN_LAUNCHER} \
        -cp ${APP_HOME}/lib/*:$PATH \
        ${JVM_OPT} \
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
        -Dlogback.configurationFile=${APP_HOME}/conf/logback.xml \
        -cp ${APP_HOME}/lib/*:${APP_HOME}/plugin/*:$PATH \
        ${JVM_OPT} \
        $1
}

if [ "$1" = start ]; then
    start
elif [ "$1" = stop ]; then 
    stop
elif [ "$1" = "-m" ]; then
    main "$2"
else
    usage
fi

