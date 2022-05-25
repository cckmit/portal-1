#!/bin/sh
set -e
cd "${0%/*}"
. ./_variables.sh
. ./node/is-node.sh
. ./docker/is-docker.sh

Build () {
  echo "> Build"

  if ! IsNodeVersion 16 ; then echo ">! Required node 16 to build this app"; die; fi

  echo "> List versions"
  ./versions.sh || die

  echo "> Install project"
  cd "$dir_root"
  npx yarn install --immutable || die
  cd "$dir_scripts"

  echo "> Build '@protei-portal/app-portal'"
  cd "$dir_root"
  npx yarn workspace @protei-portal/app-portal run build --no-color || die
  cd "$dir_scripts"

  echo "> Build done"
}

BuildByDocker () {
  echo "> Build by docker"

  if ! IsDocker ; then echo ">! Required docker to build this app"; die; fi

  echo " > Cleanup"
  cd "$dir_root"
  rm -rf "${dir_packages}"/app/portal/build || die
  cd "$dir_scripts"

  echo "> Run docker build"
  cd "$dir_root"
  DOCKER_BUILDKIT=1 docker build --rm -o "${dir_packages}"/app/portal/build -f "${dir_scripts}"/docker/build.Dockerfile . || die
  cd "$dir_scripts"

  echo "> Build by docker done"
}

BuildAndRun () {
  echo "> Build and run by docker"

  if ! IsDocker ; then echo ">! Required docker to build and run this app"; die; fi

  cd "$dir_root"
  docker stop $(docker ps -a -q --filter name=portalui --format="{{.ID}}") || true
  DOCKER_BUILDKIT=1 docker build --rm -t portaluiimg -f "${dir_scripts}"/docker/build-image.Dockerfile . || die
  docker run --rm -d -p 80:80 -p 443:443 --name portalui portaluiimg || die
  cd "$dir_scripts"
}

die () {
  echo "> Build failed"
  exit 1
}

if [ "$1" = "docker" ]; then
  BuildByDocker
elif [ "$1" = "run" ]; then
  BuildAndRun
else
  Build
fi
