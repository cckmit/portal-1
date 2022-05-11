import { ContainerModule } from "inversify"
import { EventBusModule, EventBusModule$type } from "@protei-libs/module"
import { unitDeliveryModule, UnitDeliveryModule, UnitDeliveryModule$type } from "../jsmodule"

export const iocUnitDelivery = new ContainerModule((bind) => {
  bind<EventBusModule>(EventBusModule$type).toConstantValue(unitDeliveryModule)
  bind<UnitDeliveryModule>(UnitDeliveryModule$type).toConstantValue(unitDeliveryModule)

  // bind<Test1Service>(Test1Service$type).to(Test1ServiceImpl)
  //
  // bind<Test1Store>(Test1Store$type).toConstantValue(test1Store)
})
