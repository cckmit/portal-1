FROM node:16-alpine AS build-stage
WORKDIR /usr/app
COPY . .
RUN sh ./web/scripts/build.sh

FROM scratch AS export-stage
COPY --from=build-stage /usr/app/web/packages/app/portal/build /
