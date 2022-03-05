import { EventBus, EventBusEvent, EventListener, EventTopic } from "@protei-libs/eventbus"
import { Unsubscribe } from "@protei-libs/types"

export const EventBusModule$type = Symbol("EventBusModule")

export interface EventBusModule {
  addExternalEventbus(eventbus: EventBus<EventBusEvent<unknown>>): Unsubscribe

  fireEvent(event: EventBusEvent<unknown>): void

  listenEvent<T extends EventBusEvent<unknown>>(
    config: { topic: EventTopic },
    listener: EventListener<T>,
  ): Unsubscribe

  listenEventSync<T extends EventBusEvent<unknown>>(
    config: { topic: EventTopic },
    listener: EventListener<T>,
  ): Unsubscribe
}
