import { Unsubscribe } from "@protei-libs/types"
import { scheduleMacroTask, scheduleNow, Scheduler } from "@protei-libs/scheduler"
import {
  EventBus,
  EventBusEvent,
  EventListener,
  EventTopic,
  newEventBus,
} from "@protei-libs/eventbus"
import { EventBusModule } from "./EventBusModule"
import { arrayFilterInPlace } from "../util/array"

export class EventBusModuleImpl implements EventBusModule {
  private internalEventBus = newEventBus<EventBusEvent<unknown>>()
  private externalEventBuses: Array<EventBus<EventBusEvent<unknown>>> = []
  private externalListeners: Array<(event: EventBusEvent<unknown>) => void> = []

  fireEvent(event: EventBusEvent<unknown>): void {
    this.internalEventBus.send(event.type, event)
    for (const externalEventBus of this.externalEventBuses) {
      externalEventBus.send(event.type, event)
    }
  }

  fireEventOnlyInternal(event: EventBusEvent<unknown>): void {
    this.internalEventBus.send(event.type, event)
  }

  listenEvent<T extends EventBusEvent<unknown>>(
    config: { topic: EventTopic },
    listener: EventListener<T>,
  ): Unsubscribe {
    return this.subscribe(this.internalEventBus, config, listener, scheduleMacroTask)
  }

  listenEventSync<T extends EventBusEvent<unknown>>(
    config: { topic: EventTopic },
    listener: EventListener<T>,
  ): Unsubscribe {
    return this.subscribe(this.internalEventBus, config, listener, scheduleNow)
  }

  private subscribe<T extends EventBusEvent<unknown>>(
    eventbus: EventBus<EventBusEvent<unknown>>,
    config: { topic: EventTopic },
    listener: EventListener<T>,
    scheduler: Scheduler,
  ): Unsubscribe {
    const listenerScheduler = (event: EventBusEvent<unknown>) => {
      scheduler(() => {
        listener(event as T)
      })
    }
    return eventbus.subscribe(config.topic, listenerScheduler)
  }

  addExternalEventbus(eventbus: EventBus<EventBusEvent<unknown>>): Unsubscribe {
    this.externalEventBuses.push(eventbus)
    const registration = eventbus.subscribeBroadcast((event) => {
      this.onExternalEvent(event)
    })
    return () => {
      registration()
      arrayFilterInPlace(this.externalEventBuses, (eb) => eb !== eventbus)
    }
  }

  protected onExternalEvent(event: EventBusEvent<unknown>): void {
    if (this.externalListeners.length === 0) {
      this.fireEventOnlyInternal(event)
      return
    }
    for (const listener of this.externalListeners) {
      listener(event)
    }
  }

  addExternalEventListener(listener: (event: EventBusEvent<unknown>) => void): Unsubscribe {
    this.externalListeners.push(listener)
    return () => arrayFilterInPlace(this.externalListeners, (l) => l !== listener)
  }
}
