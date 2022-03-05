import { scheduleMacroTask, scheduleNow, Scheduler } from "@protei-libs/scheduler"
import { Unsubscribe } from "@protei-libs/types"
import {
  EventBus,
  EventBusEvent,
  newEventBus,
  EventListener,
  EventTopic,
} from "@protei-libs/eventbus"
import { arrayFilterInPlace } from "../util/array"
import { ModuleWithEventBus } from "./ModuleWithEventBus"
import { ModuleLoader } from "../loader"

export abstract class ModuleWithEventBusAbstractImpl<MODULE_TYPE>
  implements ModuleWithEventBus<MODULE_TYPE>
{
  abstract get loader(): ModuleLoader<MODULE_TYPE>

  private externalEventBuses: Array<EventBus<EventBusEvent<unknown>>> = []
  private internalEventBus = newEventBus<EventBusEvent<unknown>>()

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

  fireEvent(event: EventBusEvent<unknown>): void {
    this.internalEventBus.send(event.type, event)
    for (const externalEventBus of this.externalEventBuses) {
      externalEventBus.send(event.type, event)
    }
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

  protected onExternalEvent(event: EventBusEvent<unknown>): void {
    if (this.isLoaded()) {
      this.internalEventBus.send(event.type, event)
      return
    }
    if (!this.shouldLoadModuleByEvent(event)) {
      return
    }
    this.loader.load().then(() => {
      this.internalEventBus.send(event.type, event)
    })
  }

  protected isLoaded(): boolean {
    return this.loader.get() !== undefined
  }

  protected abstract shouldLoadModuleByEvent(event: EventBusEvent<unknown>): boolean
}
