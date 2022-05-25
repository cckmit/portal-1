import { ContainerModule } from "inversify"
import { EventBusModule, EventBusModule$type } from "@protei-libs/module"
import { CommonModule, commonModule, CommonModule$type } from "../jsmodule"
import { authStore, AuthStore, AuthStore$type } from "./store"
import {
  AuthService,
  AuthService$type,
  AuthServiceImpl,
  DebugService,
  DebugService$type,
  DebugServiceImpl,
  MediaService,
  MediaService$type,
  MediaServiceImpl,
} from "./service"
import {
  CompanyTransport,
  CompanyTransport$type,
  CompanyTransportImpl,
  DeliverySpecificationTransport,
  DeliverySpecificationTransport$type,
  DeliverySpecificationTransportImpl,
  HttpTransport,
  HttpTransport$type,
  HttpTransportImpl,
  PersonTransport,
  PersonTransport$type,
  PersonTransportImpl,
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
  bind<DebugService>(DebugService$type).to(DebugServiceImpl).inSingletonScope()
  bind<MediaService>(MediaService$type).to(MediaServiceImpl).inSingletonScope()

  bind<HttpTransport>(HttpTransport$type).to(HttpTransportImpl).inSingletonScope()
  bind<PortalApiTransport>(PortalApiTransport$type).to(PortalApiTransportImpl).inSingletonScope()
  bind<PortalApiRequestIdProvider>(PortalApiRequestIdProvider$type).to(PortalApiRequestIdProviderImpl).inSingletonScope()
  bind<DeliverySpecificationTransport>(DeliverySpecificationTransport$type).to(DeliverySpecificationTransportImpl).inSingletonScope()
  bind<PersonTransport>(PersonTransport$type).to(PersonTransportImpl).inSingletonScope()
  bind<CompanyTransport>(CompanyTransport$type).to(CompanyTransportImpl).inSingletonScope()
})
