import { makeLogger } from "@protei-libs/logger"
import { iocContainer } from "./ioc"
import { iocUnitTest2 } from "./ioc-module"

export function setup(): void {
  const log = makeLogger("portal.u-test2.setup")
  log.info("Setup - start")
  iocContainer.load(iocUnitTest2)
  log.info("Setup - done")
}
