import { jsonPluginDateAsISO, jsonPluginOmitNulls, makeJson } from "../../infrastructure"

export const PortalApiJson = makeJson([
  jsonPluginOmitNulls(),
  jsonPluginDateAsISO(),
])
