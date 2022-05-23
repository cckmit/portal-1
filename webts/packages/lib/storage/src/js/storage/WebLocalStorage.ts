import { StorageKey, StorageValue, WebStorage, WebStorageChangeEventListener } from "./WebStorage"
import { webInMemoryStorage } from "./WebInMemoryStorage"

const IS_LOCAL_STORAGE_SUPPORTED = "localStorage" in window && window.localStorage != null

export const webLocalStorage: WebStorage = !IS_LOCAL_STORAGE_SUPPORTED
  ? webInMemoryStorage
  : new (class implements WebStorage {
      get(key: StorageKey): StorageValue | undefined {
        return localStorage.getItem(key) || undefined
      }
      remove(key: StorageKey): StorageValue | undefined {
        const value = localStorage.getItem(key) || undefined
        localStorage.removeItem(key)
        return value
      }
      set(key: StorageKey, value: StorageValue): void {
        localStorage.setItem(key, value)
      }
      addChangeListener(listener: WebStorageChangeEventListener) {
        window.addEventListener("storage", listener)
      }
      removeChangeListener(listener: WebStorageChangeEventListener) {
        window.removeEventListener("storage", listener)
      }
    })()
