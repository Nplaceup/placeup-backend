PROFILE=${1:-prd}
BUILD_DIR=./build/libs

echo "==== Build Gradle Start... ===="
./gradlew clean build -x test || { echo "Build Failure"; exit 1; }

JAR_NAME=$(ls $BUILD_DIR/*.jar 2>/dev/null | grep -v plain | head -n 1)
if [ -z "$JAR_NAME" ]; then
  echo "Jar not found in $BUILD_DIR"
  exit 1
fi

echo "==== Stopping Previous Server... ===="
PID=$(pgrep -f $JAR_NAME)
if [ -n "$PID" ]; then
  echo "Server Process Stopped (PID: $PID)"
  kill -15 $PID
  sleep 5
else
  echo "No Progressing Server"
fi

echo "==== Server Restarting... ===="
nohup java -jar "$JAR_NAME" --spring.profiles.active="$PROFILE" > /dev/null 2>&1 &

echo "Server Started"
