#!/bin/bash

APP_NAME="admin"
LOG_FILE="$HOME/AppLogs/dontworry-ai/$APP_NAME/default.log"

tail -f "$LOG_FILE"
