import { EventSource, EventTopic } from "./EventBus"

export type EventBusEvent<T> = {
  type: EventTopic
  source: EventSource
  payload: T
}
