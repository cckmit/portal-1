import { ContainerModule } from "inversify"
import { EventBusModule, EventBusModule$type } from "@protei-libs/module"
import { UnitTest2Module, unitTest2Module, UnitTest2Module$type } from "../jsmodule"

export const iocUnitTest2 = new ContainerModule((bind) => {
  bind<EventBusModule>(EventBusModule$type).toConstantValue(unitTest2Module)
  bind<UnitTest2Module>(UnitTest2Module$type).toConstantValue(unitTest2Module)
})
