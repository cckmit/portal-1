import "reflect-metadata"
import { configure as setMobxConfiguration } from "mobx"
import { addLogConfiguration, LogLevel } from "@protei-libs/logger"
import { portalLogAppender } from "@protei-portal/common-logger"
import { setup } from "./js/setup"

console.log(
  "%cPORTAL WEB",
  "color:#FFFFFF; background-color:#182438; font-size:24px; font-weight:bold; padding:8px 12px; border-radius:8px;",
)

setMobxConfiguration({
  useProxies: "always",
  enforceActions: "always",
  isolateGlobalState: true,
})

addLogConfiguration({
  level: LogLevel.all,
  appenders: [portalLogAppender],
})

setup()
