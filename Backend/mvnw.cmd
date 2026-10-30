@echo off
setlocal enabledelayedexpansion
where mvn >nul 2>nul
if %ERRORLEVEL% EQU 0 (
  mvn -f "%~dp0pom.xml" %*
  exit /b !ERRORLEVEL!
)
set "MVN_VERSION=3.9.9"
set "MVN_BASE=%USERPROFILE%\.m2\wrapper\dists\apache-maven-%MVN_VERSION%"
set "MVN_HOME=%MVN_BASE%\apache-maven-%MVN_VERSION%"
set "MVN_BIN=%MVN_HOME%\bin\mvn.cmd"
if not exist "%MVN_BIN%" (
  if not exist "%MVN_BASE%" mkdir "%MVN_BASE%"
  set "MVN_ZIP=%MVN_BASE%\apache-maven-%MVN_VERSION%-bin.zip"
  echo Maven nao instalado. Tentando baixar Maven %MVN_VERSION%...
  powershell -NoProfile -ExecutionPolicy Bypass -Command "Invoke-WebRequest -Uri 'https://archive.apache.org/dist/maven/maven-3/%MVN_VERSION%/binaries/apache-maven-%MVN_VERSION%-bin.zip' -OutFile '!MVN_ZIP!'"
  if errorlevel 1 (
    echo Falha ao baixar Maven. Instale Maven 3.9+ e tente novamente.
    exit /b 1
  )
  powershell -NoProfile -ExecutionPolicy Bypass -Command "Expand-Archive -LiteralPath '!MVN_ZIP!' -DestinationPath '%MVN_BASE%' -Force"
  if errorlevel 1 exit /b 1
)
call "%MVN_BIN%" -f "%~dp0pom.xml" %*
exit /b %ERRORLEVEL%
