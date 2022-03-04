import { EventBus, EventBusEvent, EventSource } from "@protei-libs/eventbus"
import { ConnectorMessage } from "../connector/ConnectorMessage"

export function onEventbusEvent(
  eventbus: EventBus<EventBusEvent<unknown>>,
  message: ConnectorMessage,
  allowedSources?: Array<EventSource>,
): void {
  if (message.type !== "portal-eventbus-event") {
    return
  }
  const msg = message as ConnectorMessage<EventBusEvent<unknown>>
  if (!("payload" in msg)) {
    return
  }
  const event = msg.payload
  if (!("type" in event)) {
    return
  }
  if (!("source" in event)) {
    return
  }
  if (allowedSources !== undefined && !allowedSources.includes(event.source)) {
    return
  }
  eventbus.send(event.type, event)
}
