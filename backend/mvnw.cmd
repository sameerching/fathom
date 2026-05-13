@ECHO OFF
SETLOCAL
SET WRAPPER_DIR=%~dp0.mvn\wrapper
SET WRAPPER_JAR=%WRAPPER_DIR%\maven-wrapper.jar
SET WRAPPER_PROPS=%WRAPPER_DIR%\maven-wrapper.properties
IF NOT EXIST "%WRAPPER_JAR%" (
  FOR /F "tokens=1,* delims==" %%A IN (%WRAPPER_PROPS%) DO (
    IF "%%A"=="wrapperUrl" SET WRAPPER_URL=%%B
  )
  powershell -Command "Invoke-WebRequest -Uri '%WRAPPER_URL%' -OutFile '%WRAPPER_JAR%'"
)
java -classpath "%WRAPPER_JAR%" "-Dmaven.multiModuleProjectDirectory=%~dp0" org.apache.maven.wrapper.MavenWrapperMain %*
