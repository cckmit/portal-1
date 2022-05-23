import { ModuleLoader } from "../loader"

export interface Module<MODULE_TYPE> {
  get loader(): ModuleLoader<MODULE_TYPE>
}
