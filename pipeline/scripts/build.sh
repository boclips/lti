#!/usr/bin/env bash

set -eu

echo "Adding bit.dev to npm registry"
echo "always-auth=true" >> ~/.npmrc
echo "@bit:registry=https://node.bit.dev" >> ~/.npmrc
echo "//node.bit.dev/:_authToken={$BIT_TOKEN}" >> ~/.npmrc
echo "Completed adding bit.dev to npm registry"

export GRADLE_USER_HOME=".gradle"

version=$(cat version/tag)

(
cd source
./gradlew -Pversion="$version" clean build --rerun-tasks
)

cp -a source/* dist/
