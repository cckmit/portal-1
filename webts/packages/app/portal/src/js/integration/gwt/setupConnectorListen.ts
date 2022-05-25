import { EventBus, EventBusEvent } from "@protei-libs/eventbus"
import { EventBusSourcePortalUiGwt } from "@protei-portal/common"
import { setupConnectorEventbusListen } from "../connector/setupConnectorEventbusListen"
import { ConnectorMessage } from "../connector/ConnectorMessage"
import { onEventbusEvent } from "./onEventbusEvent"
import { onMount } from "./onMount"

export function setupConnectorListen(eventbus: EventBus<EventBusEvent<unknown>>): void {
  if (!window.Protei_PORTAL_Bridge) {
    window.Protei_PORTAL_Bridge = {}
  }
  setupConnectorEventbusListen(makeTarget(), onMessage(eventbus))
}

function makeTarget() {
  return {
    addListener(listener: (message: ConnectorMessage) => unknown) {
      window.Protei_PORTAL_Bridge && (window.Protei_PORTAL_Bridge.messageIn = listener)
    },
    removeListener(listener: (message: ConnectorMessage) => unknown) {
      window.Protei_PORTAL_Bridge && (window.Protei_PORTAL_Bridge.messageIn = undefined)
    },
  }
}

function onMessage(eventbus: EventBus<EventBusEvent<unknown>>) {
  return (message: ConnectorMessage) => {
    switch (message.type) {
      case "eventbus-event":
        onEventbusEvent(eventbus, message, [EventBusSourcePortalUiGwt])
        break
      case "mount":
        onMount(message)
        break
    }
  }
}
