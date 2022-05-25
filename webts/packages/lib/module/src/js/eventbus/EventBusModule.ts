import { EventBus, EventBusEvent, EventListener, EventTopic } from "@protei-libs/eventbus"
import { Unsubscribe } from "@protei-libs/types"

export const EventBusModule$type = Symbol("EventBusModule")

export interface EventBusModule {
  fireEvent(event: EventBusEvent<unknown>): void

  fireEventOnlyInternal(event: EventBusEvent<unknown>): void

  listenEvent<T extends EventBusEvent<unknown>>(
    config: { topic: EventTopic },
    listener: EventListener<T>,
  ): Unsubscribe

  listenEventSync<T extends EventBusEvent<unknown>>(
    config: { topic: EventTopic },
    listener: EventListener<T>,
  ): Unsubscribe

  addExternalEventbus(eventbus: EventBus<EventBusEvent<unknown>>): Unsubscribe

  addExternalEventListener(listener: (event: EventBusEvent<unknown>) => void): Unsubscribe
}
