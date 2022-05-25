import { useEffect } from "react"
import { useForceUpdate } from "@protei-libs/react"
import { ModuleLoader } from "../loader"

export function useModuleLoader<MODULE_TYPE>(opts: {
  moduleLoader: ModuleLoader<MODULE_TYPE>
  noLoad?: boolean
}): MODULE_TYPE | undefined {
  const { moduleLoader, noLoad } = opts
  const module = moduleLoader.get()
  const forceUpdate = useForceUpdate()

  useEffect(() => {
    if (noLoad || module) {
      return
    }
    void moduleLoader.load().then(forceUpdate)
  }, [noLoad, module, moduleLoader, forceUpdate])

  return module
}
