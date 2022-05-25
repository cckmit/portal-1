import { ContainerModule } from "inversify"
import { LangService, LangService$type, LangServiceImpl } from "./service/LangService"
import { langStore, LangStore, LangStore$type } from "./store"

export const iocCommonLang = new ContainerModule((bind) => {
  bind<LangService>(LangService$type).to(LangServiceImpl).inSingletonScope()
  bind<LangStore>(LangStore$type).toDynamicValue(() => langStore)
})
