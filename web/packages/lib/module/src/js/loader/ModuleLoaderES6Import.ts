import { ModuleLoader } from "./ModuleLoader"

export class ModuleLoaderES6Import<MODULE_TYPE> implements ModuleLoader<MODULE_TYPE> {
  get(): MODULE_TYPE | undefined {
    return this.memoryCache
  }

  async load(): Promise<MODULE_TYPE> {
    if (this.memoryCache) {
      return this.memoryCache
    }
    if (!this.loadPromise) {
      this.loadPromise = this.moduleImportFunction()
    }
    this.memoryCache = await this.loadPromise
    return this.memoryCache
  }

  constructor(opts: { moduleImportFunction: () => Promise<MODULE_TYPE> }) {
    this.moduleImportFunction = opts.moduleImportFunction
  }

  private readonly moduleImportFunction: () => Promise<MODULE_TYPE>
  private loadPromise: Promise<MODULE_TYPE> | undefined = undefined
  private memoryCache: MODULE_TYPE | undefined = undefined
}
