#!/bin/bash

set -eu

keyfile="$(mktemp)"

base64 -d > "$keyfile" <<< "$DEPLOY_BASE64_SSH_KEY"

ssh "${!1}" -q -i "$keyfile" -o SendEnv=TRAVIS_COMMIT -o StrictHostKeyChecking=no <<'EOF'

set -eu

cd shinyproxy

git fetch

if [[ "$(git rev-parse HEAD)" != "$(git rev-parse "$TRAVIS_COMMIT")" ]] && git merge-base --is-ancestor "$TRAVIS_COMMIT" HEAD; then
  echo "On older version than HEAD; exiting"
  exit
fi

git reset --hard "$TRAVIS_COMMIT"

./buildshinyproxy.sh

EOF

rm "$keyfile"
