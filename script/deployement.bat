@REM Script pour transformer le projet en jar

set SRC_DIR=D:/JASON/programmes/JSP/sprint/framework2529/src
set TEMP_SRC=D:/JASON/programmes/JSP/sprint/framework2529/temp-src
set BIN_DIR=D:/JASON/programmes/JSP/sprint/framework2529/bin
set LIB_DIR=C:/Program Files/Apache Software Foundation/Tomcat 10.1/lib/servlet-api.jar
set WORK_DIR=D:/JASON/programmes/JSP/sprint/framework2529

mkdir "%TEMP_SRC%"

FOR /R "%SRC_DIR%" %%F IN (*.java) DO (
    copy "%%F" "%TEMP_SRC%"
)

cd "%TEMP_SRC%"
javac -sourcepath "%TEMP_SRC%" -d "%BIN_DIR%" -cp "%LIB_DIR%" *.java

cd "%BIN_DIR%"
jar -cvf "%WORK_DIR%/framework2529.jar" -C "%BIN_DIR%" .