import { makeLogger } from "@protei-libs/logger"
import { EventBus, EventBusEvent } from "@protei-libs/eventbus"
import { setupConnectorForward } from "./setupConnectorForward"
import { setupConnectorListen } from "./setupConnectorListen"

const log = makeLogger("portal.integration")

export function setupIntegrationPortalGwt(eventbus: EventBus<EventBusEvent<unknown>>): void {
  log.info("Integration Portal GWT: will use window")
  setupConnectorForward(eventbus)
  setupConnectorListen(eventbus)
}
