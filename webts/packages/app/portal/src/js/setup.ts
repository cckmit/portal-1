import { makeLogger } from "@protei-libs/logger"
import { EventBus, EventBusEvent, newEventBus } from "@protei-libs/eventbus"
import { setupIntegrationPortalGwt } from "./integration/gwt/setupIntegrationPortalGwt"
import { commonModule } from "@protei-portal/common/src/jsmodule"
import { unitDeliveryModule } from "@protei-portal/unit-delivery/src/jsmodule"

export function setup(): void {
  const log = makeLogger("portal.app.setup")
  log.info("Setup - start")
  const eventbus = setupEventbus()
  setupIntegrationPortalGwt(eventbus)
  void commonModule.loader.load()
  commonModule.addExternalEventbus(eventbus)
  unitDeliveryModule.addExternalEventbus(eventbus)
  log.info("Setup - done")
}

function setupEventbus(): EventBus<EventBusEvent<unknown>> {
  const eventbus = newEventBus<EventBusEvent<unknown>>()
  const eventbusLog = makeLogger("portal.eventbus")
  eventbus.subscribeBroadcast((event) => {
    eventbusLog.debug("[BUS] {}", event)
  })
  return eventbus
}
