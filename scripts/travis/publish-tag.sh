#!/usr/bin/env bash

# Ensure that this is being run by Travis
if [ "$TRAVIS" != "true" ] || [ "$USER" != "travis" ]; then
  echo "This script should only be run by Travis CI."
  exit 2
fi

# Ensure that the tag is named properly as a semver tag
if [[ ! "$TRAVIS_TAG" =~ ^v[0-9]+\.[0-9]+\.[0-9]+$ ]]; then
  echo "Tag $TRAVIS_TAG is NOT a valid semver tag (vX.Y.Z), please delete this tag."
  exit 1
fi

# Ensure that the script is being run from the root project directory
PROPERTIES_FILE='gradle.properties'
if [ ! -f "$PROPERTIES_FILE" ]; then
  echo "Could not find $PROPERTIES_FILE, are you sure this is being run from the root project directory?"
  echo "PWD: ${PWD}"
  exit 1
fi

# Determine the version being published
VERSION=$(awk 'BEGIN { FS = "=" }; $1 == "version" { print $2 }' $PROPERTIES_FILE | awk '{ print $1 }')
if [ -z "$VERSION" ]; then
  echo "Could not read the version from $PROPERTIES_FILE, please fix it and try again."
  exit 1
fi

# Ensure the tag corresponds to the current version
EXPECTED_TAG="v$VERSION"
if [ "$TRAVIS_TAG" != "$EXPECTED_TAG" ]; then
  echo "Attempting to publish ParSeq version $VERSION from tag $TRAVIS_TAG is illegal."
  echo "Please delete this tag and publish instead from tag $EXPECTED_TAG"
  exit 1
fi

# Ensure the commit environment variable exists
if [ -z "$TRAVIS_COMMIT" ]; then
  echo 'Cannot find environment variable named TRAVIS_COMMIT, did the Travis API change?'
  exit 1
fi

# Ensure that the tag commit is an ancestor of master
git fetch origin master:master
git merge-base --is-ancestor $TRAVIS_COMMIT master
if [ $? -ne 0 ]; then
  echo "Tag $TRAVIS_TAG is NOT an ancestor of master!"
  echo 'Please delete this tag and instead create a tag off a master commit.'
  exit 1
fi

# Publish to JFrog Artifactory
echo "All checks passed, attempting to publish ParSeq $VERSION to JFrog Artifactory..."
./gradlew -Prelease artifactoryPublish

if [ $? = 0 ]; then
  echo "Successfully published ParSeq $VERSION to JFrog Artifactory."
else
  # We used to roll back Bintray uploads on failure to publish, but it's not clear if this is needed for JFrog.
  # TODO: If "partial uploads" can occur for JFrog, then here we would roll back the upload via the JFrog REST API.
  # We did this before using: curl -X DELETE --user ${BINTRAY_USER}:${BINTRAY_KEY} --fail $DELETE_VERSION_URL

  echo 'Failed to publish to JFrog Artifactory.'
  echo "You can check https://linkedin.jfrog.io/ui/repos/tree/General/parseq to ensure that $VERSION is not present."
  echo 'Please retry the upload by restarting this Travis job.'

  exit 1
fi
