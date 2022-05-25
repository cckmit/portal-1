FROM node:16-alpine AS build-stage
WORKDIR /usr/app
COPY . .
RUN sh ./webts/scripts/build.sh

FROM scratch AS export-stage
COPY --from=build-stage /usr/app/webts/packages/app/portal/build /
