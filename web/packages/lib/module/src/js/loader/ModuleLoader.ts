export interface ModuleLoader<MODULE_TYPE> {
  get(): MODULE_TYPE | undefined

  load(): Promise<MODULE_TYPE>
}
