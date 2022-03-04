import { observer } from "mobx-react-lite"
import { useModuleLoader } from "@protei-libs/module"
import { unitTest2Module } from "../module"

export const Test2ComponentAsync = observer(function Test2ComponentAsync() {
  const module = useModuleLoader({ moduleLoader: unitTest2Module.loader })
  return module ? <module.Test2Component /> : <div>Loading...</div>
})
