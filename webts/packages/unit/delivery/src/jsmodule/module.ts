import {
  ModuleLoaderES6Import,
  ModuleWithEventBus,
  ModuleWithEventBusAbstractImpl,
} from "@protei-libs/module"
import { EventBusEvent } from "@protei-libs/eventbus"
import { loadModuleEvents } from "./loadModuleEvents"

export const UnitDeliveryModule$type = Symbol("UnitDeliveryModule")

export interface UnitDeliveryModule extends ModuleWithEventBus<typeof import("../index")> {}

class UnitDeliveryModuleImpl
  extends ModuleWithEventBusAbstractImpl<typeof import("../index")>
  implements UnitDeliveryModule
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

export const unitDeliveryModule: UnitDeliveryModule = new UnitDeliveryModuleImpl()
