#!/bin/bash

set -eu

ssh-agent -l > /dev/null 2>&1 || eval "$(ssh-agent -s)"
base64 -d <<< "$DEPLOY_BASE64_SSH_KEY" | ssh-add -

export TRAVIS_DOCKER_IMAGE="$1"
shift # removes $1 from arguments for ssh

ssh "$@" -q -o SendEnv=TRAVIS_COMMIT -o SendEnv=TRAVIS_DOCKER_IMAGE -o StrictHostKeyChecking=no <<'EOF'

set -eu

cd shinyproxy

git fetch

if [[ "$(git rev-parse HEAD)" != "$(git rev-parse "$TRAVIS_COMMIT")" ]] && git merge-base --is-ancestor "$TRAVIS_COMMIT" HEAD; then
  echo "On older version than HEAD; exiting"
  exit
fi

git reset --hard "$TRAVIS_COMMIT"

./buildshinyproxy.sh

sudo docker build -t proskriptive.azurecr.io/"$TRAVIS_DOCKER_IMAGE" .
sudo docker push proskriptive.azurecr.io/"$TRAVIS_DOCKER_IMAGE"

EOF

rm "$keyfile"
