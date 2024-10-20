@echo off  
:: 设置Elasticsearch的安装目录  
set ELASTICSEARCH_HOME=C:\netmap\elk\elasticsearch
  
:: 设置Elasticsearch服务的名称  
set ELASTICSEARCH_SERVICE_NAME=elasticsearch-service-x64
  

setlocal  
:: 查询Elasticsearch服务是否存在  
sc query %ELASTICSEARCH_SERVICE_NAME% | findstr /I /C:"SERVICE_NAME" > nul
if errorlevel 1 (
    echo Service does not exist.
	:: 导航到Elasticsearch的bin目录  
cd /d "%ELASTICSEARCH_HOME%\bin" 
:: 调用elasticsearch-service.bat脚本来安装服务    
elasticsearch-service.bat install 

:: 启动服务  
net start "%ELASTICSEARCH_SERVICE_NAME%"
) else (
:: 提示用户服务已安装  
echo Elasticsearch install complete。 
    net start "%ELASTICSEARCH_SERVICE_NAME%"  
)
endlocal
  
 
  
 

  