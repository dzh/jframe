echo off
rem bat file directory

if "%1"=="" (
	echo "Usage: %0  {start|stop}"
	pause
	exit /B 0
)

cd %~dp0
cd ..
set APP_HOME=%cd%
rem remove trail slash
if %APP_HOME:~-1%==\ SET APP_HOME=%APP_HOME:~0,-1%
rem replace \ with /
set APP_HOME=%APP_HOME:\=/%
echo "Application Home %APP_HOME%"

if "%1"=="start" (
	echo "start"
	pause
	set PID_PATH=%APP_HOME%/temp/daemon.pid
	if exist %PID_PATH% (
		echo "Application has started!"
	)
	else (
		set CLASSPATH=%APP_HOME%/lib/*;%CLASSPATH%
		rem -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=127.0.0.1:6000,suspend=y ^
		start /min java -Dapp.home=%APP_HOME% -Dlogback.configurationFile=%APP_HOME%/conf/logback-daemon.xml ^
		-cp %CLASSPATH% jframe.launcher.Main &
		rem echo Starting Successfully.
	)
)
	
if "%1"=="stop" (
	set PID_PATH=%APP_HOME%/temp/app.pid
	if exist %PID_PATH% ( 
		for /f %%i in (%PID_PATH%) do @set PID=%PID% %%i
		tskill %PID%
		del %PID_PATH%
		echo "Application process shutdown finished!"
	)
	else (
		echo "Not found app.pid file!"
	)

	set PID_PATH=%APP_HOME%/temp/daemon.pid
	if exist %PID_PATH% (
		for /f %%i in (%PID_PATH%) do @set PID=%PID% %%i
		tskill %PID%
		del %PID_PATH%
		echo "Daemon process shutdown finished!"
	)
) 
else (
	echo "Usage: %0  {start|stop} "
)
pause