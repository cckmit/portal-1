import { AnyToVoidFunction, Unsubscribe } from "@protei-libs/types"

export interface ModuleLoader<MODULE_TYPE> {
  get(): MODULE_TYPE | undefined

  load(): Promise<MODULE_TYPE>

  addOnLoadedListener(listener: AnyToVoidFunction): Unsubscribe
}
