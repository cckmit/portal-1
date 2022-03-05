import { makeLogger } from "@protei-libs/logger"
import { iocContainerCommon } from "@protei-portal/common"
import { iocContainer } from "./ioc"
import { iocUnitTest2 } from "./ioc-module"

export function setup(): void {
  const log = makeLogger("portal.u-test2.setup")
  log.info("Setup - start")
  iocContainer.load(iocUnitTest2)
  iocContainer.parent = iocContainerCommon
  log.info("Setup - done")
}
