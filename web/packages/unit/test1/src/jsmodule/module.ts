import {
  ModuleLoaderES6Import,
  ModuleWithEventBus,
  ModuleWithEventBusTemplate,
} from "@protei-libs/module"
import { EventBusEvent } from "@protei-libs/eventbus"
import { loadModuleEvents } from "./loadModuleEvents"

export const UnitTest1Module$type = Symbol("UnitTest1Module")

export interface UnitTest1Module extends ModuleWithEventBus<typeof import("../index")> {}

class UnitTest1ModuleImpl
  extends ModuleWithEventBusTemplate<typeof import("../index")>
  implements UnitTest1Module
{
  readonly loader = new ModuleLoaderES6Import({
    moduleImportFunction: () => import("../index"),
  })

  protected shouldLoadModuleByEvent(event: EventBusEvent<unknown>): boolean {
    return loadModuleEvents.some((lme) => {
      return lme.type === event.type && (lme.source === undefined || lme.source === event.source)
    })
  }
}

export const unitTest1Module: UnitTest1Module = new UnitTest1ModuleImpl()
