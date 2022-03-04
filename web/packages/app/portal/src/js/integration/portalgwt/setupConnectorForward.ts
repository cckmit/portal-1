import { EventBus, EventBusEvent } from "@protei-libs/eventbus"
import { EventBusSourcePortalUi } from "@protei-portal/common-model"
import { ConnectorMessage } from "../connector/ConnectorMessage"
import { setupConnectorEventbusForward } from "../connector/setupConnectorEventbusForward"

export function setupConnectorForward(eventbus: EventBus<EventBusEvent<unknown>>): void {
  if (!window.ProteiPortalGwtBridge) {
    window.ProteiPortalGwtBridge = {}
  }
  setupConnectorEventbusForward(makeTarget(), targetSerialize(), eventbus, {
    eventFilter: makeFilter(),
  })
}

function makeTarget() {
  return {
    send(message: ConnectorMessage) {
      window.ProteiPortalGwtBridge?.messageOut?.(message)
    },
  }
}

function targetSerialize() {
  return (message: ConnectorMessage) => message
}

function makeFilter() {
  return (event: EventBusEvent<unknown>) => {
    return event.source === EventBusSourcePortalUi
  }
}
