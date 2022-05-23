import { EventBus, EventBusEvent } from "@protei-libs/eventbus"
import { EventBusSourcePortalUi } from "@protei-portal/common"
import { ConnectorMessage } from "../connector/ConnectorMessage"
import { setupConnectorEventbusForward } from "../connector/setupConnectorEventbusForward"

export function setupConnectorForward(eventbus: EventBus<EventBusEvent<unknown>>): void {
  if (!window.Protei_PORTAL_Bridge) {
    window.Protei_PORTAL_Bridge = {}
  }
  setupConnectorEventbusForward(makeTarget(), targetSerialize(), eventbus, {
    eventFilter: makeFilter(),
  })
}

function makeTarget() {
  return {
    send(message: ConnectorMessage) {
      window.Protei_PORTAL_Bridge?.messageOut?.(message)
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
