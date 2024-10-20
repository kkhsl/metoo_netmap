@echo off
for /f "tokens=1,2,3 delims= " %%a in ('wmic process where "name='java.exe' and ExecutablePath like '%%logstash%%'" get ProcessId^,Name^,ExecutablePath') do (
    echo Process ID: %%a
    echo Name: %%b
    echo Executable Path: %%c
	taskkill /F /PID %%c

)




