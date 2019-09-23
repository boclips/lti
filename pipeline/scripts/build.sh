#!/usr/bin/env bash

set -eu

export GRADLE_USER_HOME=".gradle"

version=$(cat version/version)

(
cd source
./gradlew -Pversion="$version" clean build --rerun-tasks
)

cp -a source/* dist/
