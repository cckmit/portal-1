import { observer } from "mobx-react-lite"
import { useModuleLoader } from "@protei-libs/module"
import { unitTest1Module } from "../module"

export const Test1ComponentAsync = observer(function Test1ComponentAsync() {
  const module = useModuleLoader({ moduleLoader: unitTest1Module.loader })
  return module ? <module.Test1Component /> : <div>Loading...</div>
})
