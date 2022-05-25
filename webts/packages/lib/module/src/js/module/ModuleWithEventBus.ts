import { Unsubscribe } from "@protei-libs/types"
import { EventBus, EventBusEvent, EventListener, EventTopic } from "@protei-libs/eventbus"
import { Module } from "./Module"
import { ModuleLoader } from "../loader"
import { EventBusModule, EventBusModuleImpl } from "../eventbus"

export interface ModuleWithEventBus<MODULE_TYPE> extends Module<MODULE_TYPE>, EventBusModule {}

export abstract class ModuleWithEventBusAbstractImpl<MODULE_TYPE>
  implements ModuleWithEventBus<MODULE_TYPE>
{
  abstract get loader(): ModuleLoader<MODULE_TYPE>

  private eventBusModule: EventBusModule

  constructor() {
    this.eventBusModule = new EventBusModuleImpl()
    this.eventBusModule.addExternalEventListener(this.onExternalEvent.bind(this))
  }

  protected onExternalEvent(event: EventBusEvent<unknown>): void {
    if (this.isLoaded()) {
      this.eventBusModule.fireEventOnlyInternal(event)
      return
    }
    if (!this.shouldLoadModuleByEvent(event)) {
      return
    }
    void this.loader.load().then(() => {
      this.eventBusModule.fireEventOnlyInternal(event)
    })
  }

  protected isLoaded(): boolean {
    return this.loader.get() !== undefined
  }

  fireEvent(event: EventBusEvent<unknown>): void {
    this.eventBusModule.fireEvent(event)
  }

  fireEventOnlyInternal(event: EventBusEvent<unknown>): void {
    this.eventBusModule.fireEventOnlyInternal(event)
  }

  listenEvent<T extends EventBusEvent<unknown>>(
    config: { topic: EventTopic },
    listener: EventListener<T>,
  ): Unsubscribe {
    return this.eventBusModule.listenEvent(config, listener)
  }

  listenEventSync<T extends EventBusEvent<unknown>>(
    config: { topic: EventTopic },
    listener: EventListener<T>,
  ): Unsubscribe {
    return this.eventBusModule.listenEventSync(config, listener)
  }

  addExternalEventbus(eventbus: EventBus<EventBusEvent<unknown>>): Unsubscribe {
    return this.eventBusModule.addExternalEventbus(eventbus)
  }

  addExternalEventListener(listener: (event: EventBusEvent<unknown>) => void): Unsubscribe {
    return this.eventBusModule.addExternalEventListener(listener)
  }

  protected abstract shouldLoadModuleByEvent(event: EventBusEvent<unknown>): boolean
}
