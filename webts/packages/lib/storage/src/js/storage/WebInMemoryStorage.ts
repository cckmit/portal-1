import { StorageKey, StorageValue, WebStorage, WebStorageChangeEventListener } from "./WebStorage"

export const webInMemoryStorage: WebStorage = new (class implements WebStorage {
  private _storage = new Map<StorageKey, StorageValue>()

  get(key: StorageKey): StorageValue | undefined {
    return this._storage.get(key) || undefined
  }
  remove(key: StorageKey): StorageValue | undefined {
    const value = this._storage.get(key) || undefined
    this._storage.delete(key)
    return value
  }
  set(key: StorageKey, value: StorageValue): void {
    this._storage.set(key, value)
  }
  addChangeListener(listener: WebStorageChangeEventListener) {
    // noop
  }
  removeChangeListener(listener: WebStorageChangeEventListener) {
    // noop
  }
})()
