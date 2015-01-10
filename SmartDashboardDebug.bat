@echo off
title SmartDashboard Debugger

:start
xcopy lib\*.dll . /Y

echo Starting SmartDashboard...
echo Press Ctrl+C to quit
echo.
java -jar SmartDashboard.jar
echo.
echo Press any key to restart SmartDashboard.
pause
cls
goto start
