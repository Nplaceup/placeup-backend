BUILD_DIR=./build/libs
JAR_NAME=$(ls $BUILD_DIR/*-SNAPSHOT.jar | grep -v plain | head -n 1)

echo "==== Stopping Server ===="

PID=$(pgrep -f "$JAR_NAME")

if [ -n "$PID" ]; then
  echo "Found Server Process (PID: $PID), stopping..."
  kill -15 $PID
  sleep 5
  echo "Server Stopped"
else
  echo "No Running Server Found"
fi
