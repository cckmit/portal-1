import { ContainerModule } from "inversify"
import { EventBusModule, EventBusModule$type } from "@protei-libs/module"
import { CommonModule, commonModule, CommonModule$type } from "../jsmodule"
import { authStore, AuthStore, AuthStore$type } from "./store"
import { AuthService, AuthService$type, AuthServiceImpl } from "./service"

export const iocCommon = new ContainerModule((bind) => {
  bind<EventBusModule>(EventBusModule$type).toConstantValue(commonModule)
  bind<CommonModule>(CommonModule$type).toConstantValue(commonModule)

  bind<AuthService>(AuthService$type).to(AuthServiceImpl).inSingletonScope()

  bind<AuthStore>(AuthStore$type).toConstantValue(authStore)
})
