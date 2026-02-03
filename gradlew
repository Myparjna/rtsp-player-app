#!/usr/bin/env sh

# Copyright 2015 the original author or authors.
# Licensed under the Apache License, Version 2.0

APP_HOME=$(cd "$(dirname "$0")"; pwd)
JAVA_HOME=${JAVA_HOME}

# Resolve JAVA if not set
if [ -z "$JAVA_HOME" ]; then
  JAVA_CMD=$(command -v java)
else
  JAVA_CMD="$JAVA_HOME/bin/java"
fi

if [ -z "$JAVA_CMD" ]; then
  echo "ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH." >&2
  exit 1
fi

# Choose JAR
WRAPPER_JAR="$APP_HOME/gradle/wrapper/gradle-wrapper.jar"
WRAPPER_PROPERTIES="$APP_HOME/gradle/wrapper/gradle-wrapper.properties"

# Download wrapper jar if missing
if [ ! -f "$WRAPPER_JAR" ]; then
  mkdir -p "$APP_HOME/gradle/wrapper"
  BASE_URL="https://services.gradle.org"
  DIST_URL=$(grep distributionUrl "$WRAPPER_PROPERTIES" | sed 's/distributionUrl=//')
  # Fetch minimal jar from Gradle repo
  curl -sSL "$BASE_URL/wrapper/gradle-wrapper.jar" -o "$WRAPPER_JAR"
fi

exec "$JAVA_CMD" -Dorg.gradle.appname=gradlew -classpath "$WRAPPER_JAR" org.gradle.wrapper.GradleWrapperMain "$@"
