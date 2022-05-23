#!/bin/sh
set -e

echo "$(node --version)" | sed -n 's/v//p' | cut -d "." -f1
