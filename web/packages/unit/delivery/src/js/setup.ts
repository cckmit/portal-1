import { makeLogger } from "@protei-libs/logger"
import { iocContainerCommon } from "@protei-portal/common"
import { iocContainer } from "./ioc"
import { iocUnitDelivery } from "./ioc-module"

export function setup(): void {
  const log = makeLogger("portal.delivery.setup")
  log.info("Setup - start")
  iocContainer.load(iocUnitDelivery)
  iocContainer.parent = iocContainerCommon
  log.info("Setup - done")
}
