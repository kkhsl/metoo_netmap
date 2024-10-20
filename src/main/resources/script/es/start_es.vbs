' 获取当前脚本的目录路径  
Dim scriptDir, batPath  
scriptDir = Left(WScript.ScriptFullName, InStrRev(WScript.ScriptFullName, "\") - 1)  
' 构建es.bat文件的完整路径  
batPath = scriptDir & "\start_es.bat"  
' 创建一个Shell对象  
Dim shell  
Set shell = CreateObject("Shell.Application")  
' 尝试以管理员身份运行es.bat文件  
' 注意：这将会导致UAC提示，除非有特殊配置  
shell.ShellExecute "cmd.exe", "/c """ & batPath & """", "", "runas", 1  
' 清理对象  
Set shell = Nothing