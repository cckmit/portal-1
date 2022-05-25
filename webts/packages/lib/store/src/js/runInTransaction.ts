import { runInAction } from "mobx"

export function runInTransaction<T>(fn: () => T): T {
  return runInAction(fn)
}
