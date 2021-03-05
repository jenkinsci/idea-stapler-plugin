#!/bin/sh
# https://github.com/JetBrains/intellij-plugin-verifier/blob/v1.254/README.md lists file names to check
if [ -n "$(find ./build/reports/pluginVerifier -name compatibility-warnings.txt -or -name compatibility-problems.txt)" ]
then
  echo "ERROR: Compatibility issue!"
  exit 1
fi
