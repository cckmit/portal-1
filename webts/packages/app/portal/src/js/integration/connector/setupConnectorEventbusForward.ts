import { EventBus, EventBusEvent } from "@protei-libs/eventbus"
import { ConnectorMessage } from "./ConnectorMessage"

type Target<T> = {
  send(message: T): void
}

type Unsubscribe = () => void

export function setupConnectorEventbusForward<T>(
  target: Target<T>,
  targetSerialize: (message: ConnectorMessage) => T,
  eventbus: EventBus<EventBusEvent<unknown>>,
  options?: {
    eventFilter?: (event: EventBusEvent<unknown>) => boolean
    onError?: (e: Error | unknown) => void
  },
): Unsubscribe {
  const listener = (event: EventBusEvent<unknown>) => {
    try {
      if (options?.eventFilter?.(event) === false) {
        return
      }
      const message = targetSerialize({
        type: "eventbus-event",
        payload: event,
      })
      target.send(message)
    } catch (e) {
      options?.onError?.(e)
    }
  }
  eventbus.subscribeBroadcast(listener)
  return () => {
    eventbus.unsubscribeBroadcast(listener)
  }
}
