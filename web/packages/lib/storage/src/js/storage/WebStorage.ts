export type StorageKey = string
export type StorageValue = string

export interface WebStorage {
  set(key: StorageKey, value: StorageValue): void
  get(key: StorageKey): StorageValue | undefined
  remove(key: StorageKey): StorageValue | undefined
  addChangeListener(listener: WebStorageChangeEventListener): void
  removeChangeListener(listener: WebStorageChangeEventListener): void
}

export type WebStorageChangeEventListener = (event: WebStorageChangeEvent) => void

export interface WebStorageChangeEvent {
  readonly key: string | null
  readonly newValue: string | null
  readonly oldValue: string | null
}
