#!/bin/bash
set -e
# Setup script to prepare Android SDK/NDK and Java for building Pixel Spacebase
# Installs OpenJDK 8, Android command line tools, and required SDK packages

# Determine script directory
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
ROOT_DIR="$(dirname "$SCRIPT_DIR")"

ANDROID_SDK_ROOT="$HOME/android-sdk"
ANDROID_NDK_ROOT="$HOME/android-ndk"

# Install base packages
if [ -x "$(command -v apt-get)" ]; then
  sudo apt-get update
  sudo apt-get install -y openjdk-8-jdk wget unzip
fi

mkdir -p "$ANDROID_SDK_ROOT"
cd "$ANDROID_SDK_ROOT"

if [ ! -f "cmdline-tools/bin/sdkmanager" ]; then
  wget -q https://dl.google.com/android/repository/commandlinetools-linux-9123335_latest.zip -O cmdline-tools.zip
  unzip -q cmdline-tools.zip
  mv cmdline-tools cmdline-tools-temp
  mkdir cmdline-tools
  mv cmdline-tools-temp cmdline-tools/latest
fi

export ANDROID_HOME="$ANDROID_SDK_ROOT"
export PATH="$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools:$PATH"

yes | sdkmanager --licenses || true
sdkmanager "platform-tools" "platforms;android-29" "build-tools;29.0.2" "ndk;26.2.11394342"

# Write local.properties for gradle
cat > "$ROOT_DIR/local.properties" <<PROP
sdk.dir=$ANDROID_SDK_ROOT
ndk.dir=$ANDROID_SDK_ROOT/ndk/26.2.11394342
PROP

printf '\nSetup complete.\n'
