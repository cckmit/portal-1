import { StorageKey, StorageValue, WebStorage, WebStorageChangeEventListener } from "./WebStorage"
import { webInMemoryStorage } from "./WebInMemoryStorage"

const IS_SESSION_STORAGE_SUPPORTED = "sessionStorage" in window && window.sessionStorage != null

export const webSessionStorage: WebStorage = !IS_SESSION_STORAGE_SUPPORTED
  ? webInMemoryStorage
  : new (class implements WebStorage {
      get(key: StorageKey): StorageValue | undefined {
        return sessionStorage.getItem(key) || undefined
      }
      remove(key: StorageKey): StorageValue | undefined {
        const value = sessionStorage.getItem(key) || undefined
        sessionStorage.removeItem(key)
        return value
      }
      set(key: StorageKey, value: StorageValue): void {
        sessionStorage.setItem(key, value)
      }
      addChangeListener(listener: WebStorageChangeEventListener) {
        // noop
      }
      removeChangeListener(listener: WebStorageChangeEventListener) {
        // noop
      }
    })()
