@echo off
rem bat file directory

setlocal
cd %~dp0
cd ..
set APP_HOME=%cd%
rem remove trail slash
if %APP_HOME:~-1%==\ SET APP_HOME=%APP_HOME:~0,-1%
rem replace \ with /
set APP_HOME=%APP_HOME:\=/%
echo Starting from %APP_HOME%

set PID_PATH=%APP_HOME%/temp/daemon.pid
if exist %PID_PATH% (
	echo "Application has started!"
	exit /B 0
) else (
	set CLASSPATH=%APP_HOME%/lib/*;%CLASSPATH%
        rem echo %CLASSPATH%
	rem -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=127.0.0.1:6000,suspend=y
        start /b java -Dapp.home=%APP_HOME% -Dlogback.configurationFile=%APP_HOME%/conf/logback-daemon.xml ^
        -Xmx16M -Xms16M -Xmn8M -XX:MaxPermSize=20M -XX:+HeapDumpOnOutOfMemoryError ^
        -cp "%CLASSPATH%" jframe.launcher.Main
)
endlocal




