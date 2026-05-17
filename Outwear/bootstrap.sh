#!/bin/bash
# Run this script ONCE before opening in Android Studio.
# It downloads the Gradle wrapper jar (required to build the project).

echo "Downloading Gradle wrapper jar..."
curl -L "https://raw.githubusercontent.com/gradle/gradle/v8.6.0/gradle/wrapper/gradle-wrapper.jar" \
     -o gradle/wrapper/gradle-wrapper.jar \
     --create-dirs

if [ -f "gradle/wrapper/gradle-wrapper.jar" ]; then
  echo "✓ Done! You can now open the project in Android Studio."
else
  echo "Download failed. Trying alternative..."
  # Alternative: generate via gradle if installed
  if command -v gradle &>/dev/null; then
    gradle wrapper --gradle-version=8.6
    echo "✓ Generated via local gradle installation."
  else
    echo ""
    echo "Please manually:"
    echo "1. Install Gradle from https://gradle.org/install/"
    echo "2. Run: gradle wrapper --gradle-version=8.6"
    echo "   OR copy gradle-wrapper.jar from any existing Android project."
  fi
fi
