import { AnyToVoidFunction, Unsubscribe } from "@protei-libs/types"
import { ModuleLoader } from "./ModuleLoader"
import { arrayFilterInPlace } from "../util/array"

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
    this.onLoadedListeners.forEach((l) => l())
    return this.memoryCache
  }

  addOnLoadedListener(listener: AnyToVoidFunction): Unsubscribe {
    this.onLoadedListeners.push(listener)
    return () => {
      arrayFilterInPlace(this.onLoadedListeners, (l) => l !== listener)
    }
  }

  constructor(opts: { moduleImportFunction: () => Promise<MODULE_TYPE> }) {
    this.moduleImportFunction = opts.moduleImportFunction
  }

  private readonly moduleImportFunction: () => Promise<MODULE_TYPE>
  private readonly onLoadedListeners: Array<AnyToVoidFunction> = []
  private loadPromise: Promise<MODULE_TYPE> | undefined = undefined
  private memoryCache: MODULE_TYPE | undefined = undefined
}
