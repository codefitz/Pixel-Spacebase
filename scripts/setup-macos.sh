#!/bin/bash
set -e
# Setup script to prepare Android SDK/NDK and Java for building Pixel Spacebase on macOS

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
ROOT_DIR="$(dirname "$SCRIPT_DIR")"

ANDROID_SDK_ROOT="$HOME/android-sdk"
ANDROID_NDK_VERSION="26.2.11394342"

if ! command -v brew >/dev/null; then
  echo "Homebrew is required. Install from https://brew.sh/ and try again." >&2
  exit 1
fi

# Install OpenJDK 8
if ! brew list --formula | grep -q "openjdk@8"; then
  brew install openjdk@8
fi

mkdir -p "$ANDROID_SDK_ROOT"
cd "$ANDROID_SDK_ROOT"

if [ ! -f "cmdline-tools/bin/sdkmanager" ]; then
  curl -Lo cmdline-tools.zip https://dl.google.com/android/repository/commandlinetools-mac-9123335_latest.zip
  unzip -q cmdline-tools.zip
  rm cmdline-tools.zip
  mv cmdline-tools cmdline-tools-temp
  mkdir cmdline-tools
  mv cmdline-tools-temp cmdline-tools/latest
fi

export ANDROID_HOME="$ANDROID_SDK_ROOT"
export PATH="$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools:$PATH"

yes | sdkmanager --licenses || true
sdkmanager "platform-tools" "platforms;android-29" "build-tools;29.0.2" "ndk;$ANDROID_NDK_VERSION"

cat > "$ROOT_DIR/local.properties" <<PROP
sdk.dir=$ANDROID_SDK_ROOT
ndk.dir=$ANDROID_SDK_ROOT/ndk/$ANDROID_NDK_VERSION
PROP

printf '\nSetup complete.\n'
