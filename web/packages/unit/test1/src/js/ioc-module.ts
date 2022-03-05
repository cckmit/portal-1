import { ContainerModule } from "inversify"
import { EventBusModule, EventBusModule$type } from "@protei-libs/module"
import { unitTest1Module, UnitTest1Module, UnitTest1Module$type } from "../jsmodule"
import { test1Store, Test1Store, Test1Store$type } from "./store/Test1Store"
import { Test1Service, Test1Service$type, Test1ServiceImpl } from "./service/Test1Service"

export const iocUnitTest1 = new ContainerModule((bind) => {
  bind<EventBusModule>(EventBusModule$type).toConstantValue(unitTest1Module)
  bind<UnitTest1Module>(UnitTest1Module$type).toConstantValue(unitTest1Module)

  bind<Test1Service>(Test1Service$type).to(Test1ServiceImpl)

  bind<Test1Store>(Test1Store$type).toConstantValue(test1Store)
})
