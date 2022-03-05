import {
  ModuleLoaderES6Import,
  ModuleWithEventBus,
  ModuleWithEventBusAbstractImpl,
} from "@protei-libs/module"
import { EventBusEvent } from "@protei-libs/eventbus"
import { loadModuleEvents } from "./loadModuleEvents"

export const UnitTest2Module$type = Symbol("UnitTest2Module")

export interface UnitTest2Module extends ModuleWithEventBus<typeof import("../index")> {}

class UnitTest2ModuleImpl
  extends ModuleWithEventBusAbstractImpl<typeof import("../index")>
  implements UnitTest2Module
{
  readonly loader = new ModuleLoaderES6Import({
    moduleImportFunction: () =>
      import("../index").then((module) => {
        module.setup()
        return module
      }),
  })

  protected shouldLoadModuleByEvent(event: EventBusEvent<unknown>): boolean {
    return loadModuleEvents.some((lme) => {
      return lme.type === event.type && (lme.source === undefined || lme.source === event.source)
    })
  }
}

export const unitTest2Module: UnitTest2Module = new UnitTest2ModuleImpl()
