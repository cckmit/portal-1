import { makeLogger } from "@protei-libs/logger"
import { iocContainerCommon } from "@protei-portal/common"
import { iocContainer } from "./ioc"
import { iocUnitTest1 } from "./ioc-module"

export function setup(): void {
  const log = makeLogger("portal.u-test1.setup")
  log.info("Setup - start")
  iocContainer.load(iocUnitTest1)
  iocContainer.parent = iocContainerCommon
  log.info("Setup - done")
}
