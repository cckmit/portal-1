import { ContainerModule } from "inversify"
import { EventBusModule, EventBusModule$type } from "@protei-libs/module"
import { CommonModule, commonModule, CommonModule$type } from "../jsmodule"
import { authStore, AuthStore, AuthStore$type } from "./store"
import { AuthService, AuthService$type, AuthServiceImpl } from "./service"
import {
  DeliverySpecificationTransport,
  DeliverySpecificationTransport$type,
  DeliverySpecificationTransportImpl,
  HttpTransport,
  HttpTransport$type,
  HttpTransportImpl,
  PortalApiRequestIdProvider,
  PortalApiRequestIdProvider$type,
  PortalApiRequestIdProviderImpl,
  PortalApiTransport,
  PortalApiTransport$type,
  PortalApiTransportImpl,
} from "./transport"

export const iocCommon = new ContainerModule((bind) => {
  bind<EventBusModule>(EventBusModule$type).toConstantValue(commonModule)
  bind<CommonModule>(CommonModule$type).toConstantValue(commonModule)

  bind<AuthStore>(AuthStore$type).toConstantValue(authStore)

  bind<AuthService>(AuthService$type).to(AuthServiceImpl).inSingletonScope()

  bind<HttpTransport>(HttpTransport$type).to(HttpTransportImpl).inSingletonScope()
  bind<PortalApiTransport>(PortalApiTransport$type).to(PortalApiTransportImpl).inSingletonScope()
  bind<PortalApiRequestIdProvider>(PortalApiRequestIdProvider$type).to(PortalApiRequestIdProviderImpl).inSingletonScope()
  bind<DeliverySpecificationTransport>(DeliverySpecificationTransport$type).to(DeliverySpecificationTransportImpl).inSingletonScope()
})
