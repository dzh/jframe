@echo off
rem bat file directory

cd %~dp0
cd ..
set APP_HOME=%cd%
rem remove trail slash
if %APP_HOME:~-1%==\ SET APP_HOME=%APP_HOME:~0,-1%
rem replace \ with /
set APP_HOME=%APP_HOME:\=/%
echo "Application Home %APP_HOME%"

set PID_PATH=%APP_HOME%/temp/app.pid
if exist %PID_PATH% ( 
	rem for /f %%i in (%PID_PATH%) do @set PID=%PID% %%i
	set /p PID=<%PID_PATH%
	rem echo %PID%
	tskill %PID%
	del %PID_PATH%
	echo "Application process shutdown finished!"
) else (
	echo "Not found app.pid file!"
)

set PID_PATH=%APP_HOME%/temp/daemon.pid
if exist %PID_PATH% (
	rem for /f %%i in (%PID_PATH%) do @set PID=%PID% %%i
	set /p PID=<%PID_PATH%
	rem echo %PID%
	tskill %PID%
	del %PID_PATH%
	echo "Daemon process shutdown finished!"
)

