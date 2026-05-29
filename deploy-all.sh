#!/bin/bash
set -e

# ==========================
# 1. 인자 처리
# ==========================
PROFILE=${1:-prd}

if [ "$PROFILE" == "dev" ]; then
  BRANCH="develop"
else
  BRANCH="main"
fi

PROJECT_DIR="$(pwd)"
RUNTIME_DIR="$HOME/running"

echo "==== Deploy Start (Profile: $PROFILE, Branch: $BRANCH) ===="

# ==========================
# 2. Git Checkout
# ==========================
echo "==== 1. Checkout Branch: $BRANCH ===="
git checkout $BRANCH || { echo "Checkout Failure"; exit 1; }

echo "==== 2. Git Pull Start ===="
git pull origin $BRANCH || { echo "Git Pull Failure"; exit 1; }

# ==========================
# 3. Build
# ==========================
echo "==== 3. Build Start ===="
./gradlew clean build -x test || { echo "Build Failure"; exit 1; }

# ==========================
# 4. Jar 경로 설정
# ==========================
API_JAR=$(ls api/build/libs/*-SNAPSHOT.jar | grep -v plain | head -n 1)
ADMIN_JAR=$(ls admin/build/libs/*-SNAPSHOT.jar | grep -v plain | head -n 1)
SCHEDULER_JAR=$(ls scheduler/build/libs/*-SNAPSHOT.jar | grep -v plain | head -n 1)

mkdir -p "$RUNTIME_DIR"

cp "$API_JAR"       "$RUNTIME_DIR/api.jar"
cp "$ADMIN_JAR"     "$RUNTIME_DIR/admin.jar"
cp "$SCHEDULER_JAR" "$RUNTIME_DIR/scheduler.jar"

# ==========================
# 5. 기존 프로세스 종료
# ==========================
echo "==== 4. Stop Previous Servers ===="

pkill -f api.jar || true
pkill -f admin.jar || true
pkill -f scheduler.jar || true

sleep 3

# ==========================
# 6. 재실행
# ==========================
echo "==== 5. Restart Servers ===="

# port 8080
nohup java -jar "$RUNTIME_DIR/api.jar" \
  --spring.profiles.active="$PROFILE" \
  > /dev/null 2>&1 &

# port 8000
nohup java -jar "$RUNTIME_DIR/admin.jar" \
  --spring.profiles.active="$PROFILE" \
  > /dev/null 2>&1 &

# port 9000
nohup java -jar "$RUNTIME_DIR/scheduler.jar" \
  --spring.profiles.active="$PROFILE" \
  > /dev/null 2>&1 &

echo "==== Deploy Complete ===="
