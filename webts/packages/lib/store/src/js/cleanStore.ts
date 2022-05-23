import { toJS } from "mobx"

export function cleanStore<T>(source: T): T {
  return toJS(source)
}
