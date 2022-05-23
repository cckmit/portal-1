FROM node:16-alpine AS build-stage
WORKDIR /usr/app
COPY . .
RUN sh ./webts/scripts/build.sh

FROM nginx:alpine
WORKDIR /opt/protei/Protei-PORTAL-UI
RUN rm -rf ./*
COPY ./nginx/portalui.conf /etc/nginx/conf.d/default.conf
COPY --from=build-stage /usr/app/webts/packages/app/portal/build/distribution .
EXPOSE 80
EXPOSE 443
