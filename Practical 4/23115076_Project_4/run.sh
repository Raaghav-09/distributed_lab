#!/usr/bin/env bash
set -euo pipefail

cd "$(dirname "$0")"
javac SocketOptionsPractical.java
java SocketOptionsPractical
