import { makeLogger } from "@protei-libs/logger"
import { iocCommonLang } from "@protei-portal/common-lang"
import { iocContainerCommon } from "./ioc"
import { iocCommon } from "./ioc-module"
import { AuthService$type } from "./service"

export function setup(): void {
  const log = makeLogger("portal.common.setup")
  log.info("Setup - start")
  iocContainerCommon.load(iocCommon)
  iocContainerCommon.load(iocCommonLang)
  iocContainerCommon.get(AuthService$type) // TODO eager
  log.info("Setup - done")
}
