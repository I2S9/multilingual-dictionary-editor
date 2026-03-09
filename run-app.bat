@echo off
set JAVA_HOME=C:\Program Files\Java\jdk-24
cd /d "%~dp0"
"C:\Program Files\Apache\maven\apache-maven-3.9.11\bin\mvn.cmd" javafx:run -pl dictionary-editor-fx
pause
