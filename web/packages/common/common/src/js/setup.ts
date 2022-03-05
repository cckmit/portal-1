import { makeLogger } from "@protei-libs/logger"
import { iocContainerCommon } from "./ioc"
import { iocCommon } from "./ioc-module"
import { AuthService$type } from "./service"

export function setup(): void {
  const log = makeLogger("portal.common.setup")
  log.info("Setup - start")
  iocContainerCommon.load(iocCommon)
  iocContainerCommon.get(AuthService$type) // TODO eager
  log.info("Setup - done")
}
