import { makeLogger } from "@protei-libs/logger"
import { scheduleMacroTask } from "@protei-libs/scheduler"
import { EventBus, EventBusEvent, newEventBus } from "@protei-libs/eventbus"
import {
  eventBusEventTest,
  EventBusEventTestType,
  EventBusSourcePortalUiGwt,
} from "@protei-portal/common-model"
import { setupIntegrationPortalGwt } from "./integration/portalgwt/setupIntegrationPortalGwt"
import { commonModule } from "@protei-portal/common/src/jsmodule"
import { unitTest1Module } from "@protei-portal/unit-test1/src/jsmodule"
import { unitTest2Module } from "@protei-portal/unit-test2/src/jsmodule"

export function setup(): void {
  const log = makeLogger("portal.app.setup")
  log.info("Setup - start")
  const eventbus = setupEventbus()
  testEventbus(eventbus)
  setupIntegrationPortalGwt(eventbus)
  commonModule.loader.load()
  commonModule.addExternalEventbus(eventbus)
  unitTest1Module.addExternalEventbus(eventbus)
  unitTest2Module.addExternalEventbus(eventbus)
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

function testEventbus(eventbus: EventBus<EventBusEvent<unknown>>): void {
  // TODO test remove later
  eventbus.subscribe(EventBusEventTestType, (event) => {
    if (event.source === EventBusSourcePortalUiGwt) {
      console.log("TEST EVENT RECEIVED AT TS", event)
      scheduleMacroTask(() => {
        eventbus.send(EventBusEventTestType, eventBusEventTest({ text: "hello from typescript" }))
      })
    }
  })
}
