import {
  ModuleLoader,
  ModuleWithEventBus,
  ModuleWithEventBusAbstractImpl,
} from "@protei-libs/module"
import { EventBusEvent } from "@protei-libs/eventbus"
import { setup } from "../js/setup"
import "../index"

export const CommonModule$type = Symbol("CommonModule")

export interface CommonModule extends ModuleWithEventBus<boolean> {}

class CommonModuleImpl extends ModuleWithEventBusAbstractImpl<boolean> implements CommonModule {
  readonly loader = new (class implements ModuleLoader<boolean> {
    get(): boolean | undefined {
      return true
    }

    load(): Promise<boolean> {
      setup()
      return Promise.resolve(true)
    }
  })()

  protected shouldLoadModuleByEvent(event: EventBusEvent<unknown>): boolean {
    return false
  }
}

export const commonModule: CommonModule = new CommonModuleImpl()
