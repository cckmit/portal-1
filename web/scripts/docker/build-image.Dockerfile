FROM node:16-alpine AS build-stage
WORKDIR /usr/app
COPY . .
RUN sh ./web/scripts/build.sh

FROM nginx:alpine
WORKDIR /opt/protei/Protei-PORTAL-UI
RUN rm -rf ./*
COPY ./nginx/portalui.conf /etc/nginx/conf.d/default.conf
COPY --from=build-stage /usr/app/web/packages/app/portal/build/distribution .
EXPOSE 80
EXPOSE 443
