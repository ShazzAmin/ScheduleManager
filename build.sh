#!/bin/bash
JARNAME="ScheduleManager"
MAINCLASS="GraphicalUserInterface.MainFrame"

cd "$(dirname "$0")"
javac -d build $(find . -name "*.java" -type f)
jar cfe bin/$JARNAME.jar $MAINCLASS -C build .
cd build/
find . -name "*.class" -type f -delete
find . -type d -empty -delete
