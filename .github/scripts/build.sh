#!/usr/bin/env bash

# Ensure that this is being run in CI by GitHub Actions
if [ "$CI" != "true" ] || [ "$GITHUB_ACTIONS" != "true" ]; then
  echo "This script should only be run in CI by GitHub Actions."
  exit 2
fi

# Ensure that the script is being run from the root project directory
PROPERTIES_FILE='gradle.properties'
if [ ! -f "$PROPERTIES_FILE" ]; then
  echo "Could not find $PROPERTIES_FILE, are you sure this is being run from the root project directory?"
  echo "PWD: ${PWD}"
  exit 1
fi

# Determine the current version
VERSION=$(awk 'BEGIN { FS = "=" }; $1 == "version" { print $2 }' $PROPERTIES_FILE | awk '{ print $1 }')
if [ -z "$VERSION" ]; then
  echo "Could not read the version from $PROPERTIES_FILE, please fix it and try again."
  exit 1
fi

# Run the actual build
./gradlew -Prelease build
