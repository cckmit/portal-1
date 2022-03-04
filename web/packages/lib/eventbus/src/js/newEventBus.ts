import { EventBus } from "./EventBus"
import { EventBusImpl } from "./EventBusImpl"

export function newEventBus<T>(): EventBus<T> {
  return new EventBusImpl<T>()
}
