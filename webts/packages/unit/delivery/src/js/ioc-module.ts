import { ContainerModule } from "inversify"
import { EventBusModule, EventBusModule$type } from "@protei-libs/module"
import { unitDeliveryModule, UnitDeliveryModule, UnitDeliveryModule$type } from "../jsmodule"
import {
  specificationsCreateStore,
  SpecificationsCreateStore,
  SpecificationsCreateStore$type,
  specificationsImportStore,
  SpecificationsImportStore,
  SpecificationsImportStore$type,
} from "./store"
import {
  SpecificationsCreateService,
  SpecificationsCreateService$type,
  SpecificationsCreateServiceImpl,
  SpecificationsImportService,
  SpecificationsImportService$type,
  SpecificationsImportServiceImpl,
} from "./service"

export const iocUnitDelivery = new ContainerModule((bind) => {
  bind<EventBusModule>(EventBusModule$type).toConstantValue(unitDeliveryModule)
  bind<UnitDeliveryModule>(UnitDeliveryModule$type).toConstantValue(unitDeliveryModule)

  bind<SpecificationsCreateStore>(SpecificationsCreateStore$type).toConstantValue(specificationsCreateStore)
  bind<SpecificationsImportStore>(SpecificationsImportStore$type).toConstantValue(specificationsImportStore)

  bind<SpecificationsCreateService>(SpecificationsCreateService$type).to(SpecificationsCreateServiceImpl).inSingletonScope()
  bind<SpecificationsImportService>(SpecificationsImportService$type).to(SpecificationsImportServiceImpl).inSingletonScope()
})
