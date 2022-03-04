import { makeLogger } from "@protei-libs/logger"
import { iocContainer } from "./ioc"
import { iocUnitTest1 } from "./ioc-module"

export function setup(): void {
  const log = makeLogger("portal.u-test1.setup")
  log.info("Setup - start")
  iocContainer.load(iocUnitTest1)
  log.info("Setup - done")
}
