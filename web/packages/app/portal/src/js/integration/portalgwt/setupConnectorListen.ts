import { EventBus, EventBusEvent } from "@protei-libs/eventbus"
import { EventBusSourcePortalUiGwt } from "@protei-portal/common-model"
import { setupConnectorEventbusListen } from "../connector/setupConnectorEventbusListen"
import { ConnectorMessage } from "../connector/ConnectorMessage"
import { onEventbusEvent } from "./onEventbusEvent"
import { onMount } from "./onMount"

export function setupConnectorListen(eventbus: EventBus<EventBusEvent<unknown>>): void {
  if (!window.ProteiPortalGwtBridge) {
    window.ProteiPortalGwtBridge = {}
  }
  setupConnectorEventbusListen(makeTarget(), onMessage(eventbus))
}

function makeTarget() {
  return {
    addListener(listener: (message: ConnectorMessage) => unknown) {
      window.ProteiPortalGwtBridge && (window.ProteiPortalGwtBridge.messageIn = listener)
    },
    removeListener(listener: (message: ConnectorMessage) => unknown) {
      window.ProteiPortalGwtBridge && (window.ProteiPortalGwtBridge.messageIn = undefined)
    },
  }
}

function onMessage(eventbus: EventBus<EventBusEvent<unknown>>) {
  return (message: ConnectorMessage) => {
    switch (message.type) {
      case "portal-eventbus-event":
        onEventbusEvent(eventbus, message, [EventBusSourcePortalUiGwt])
        break
      case "portal-mount":
        onMount(message)
        break
    }
  }
}
