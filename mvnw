#!/bin/sh
set -eu
MVN_VERSION=3.9.9
BASE_DIR="${HOME}/.m2/wrapper/dists/apache-maven-${MVN_VERSION}"
MVN_HOME="${BASE_DIR}/apache-maven-${MVN_VERSION}"
if ! command -v mvn >/dev/null 2>&1 && [ ! -x "${MVN_HOME}/bin/mvn" ]; then
  mkdir -p "${BASE_DIR}"
  ARCHIVE="${BASE_DIR}/apache-maven-${MVN_VERSION}-bin.tar.gz"
  echo "Maven não encontrado; tentando baixar Maven ${MVN_VERSION}."
  if command -v curl >/dev/null 2>&1; then
    curl -fsSL "https://archive.apache.org/dist/maven/maven-3/${MVN_VERSION}/binaries/apache-maven-${MVN_VERSION}-bin.tar.gz" -o "${ARCHIVE}"
  else
    wget -q "https://archive.apache.org/dist/maven/maven-3/${MVN_VERSION}/binaries/apache-maven-${MVN_VERSION}-bin.tar.gz" -O "${ARCHIVE}"
  fi
  tar -xzf "${ARCHIVE}" -C "${BASE_DIR}"
fi
if command -v mvn >/dev/null 2>&1; then
  exec mvn -f "$(dirname "$0")/pom.xml" "$@"
fi
exec "${MVN_HOME}/bin/mvn" -f "$(dirname "$0")/pom.xml" "$@"
