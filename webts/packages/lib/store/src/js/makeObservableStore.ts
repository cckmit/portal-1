import { observable } from "mobx"

export function makeObservableStore<T extends object>(store: T): T {
  return observable(store)
}
