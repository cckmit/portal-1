import { Unsubscribe } from "@protei-libs/types"
import { EventBusModule } from "@protei-libs/module"
import { EventBus, EventBusEvent, EventListener, EventTopic } from "@protei-libs/eventbus"

export function makeStubEventBusModule(): EventBusModule {
  return new class implements EventBusModule {
    addExternalEventListener(listener: (event: EventBusEvent<unknown>) => void): Unsubscribe {
      return () => {}
    }

    addExternalEventbus(eventbus: EventBus<EventBusEvent<unknown>>): Unsubscribe {
      return () => {}
    }

    fireEvent(event: EventBusEvent<unknown>): void {
    }

    fireEventOnlyInternal(event: EventBusEvent<unknown>): void {
    }

    listenEvent<T extends EventBusEvent<unknown>>(config: { topic: EventTopic }, listener: EventListener<T>): Unsubscribe {
      return () => {}
    }

    listenEventSync<T extends EventBusEvent<unknown>>(config: { topic: EventTopic }, listener: EventListener<T>): Unsubscribe {
      return () => {}
    }
  }
}
