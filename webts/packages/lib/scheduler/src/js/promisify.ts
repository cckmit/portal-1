import { Scheduler } from "./scheduler"

export function promisify<T>(scheduler?: Scheduler): (fn: () => T) => Promise<T> {
  return (fn) =>
    new Promise<T>((resolve, reject) => {
      function executor() {
        try {
          const result = fn()
          resolve(result)
        } catch (e) {
          reject(e)
        }
      }
      if (scheduler) {
        scheduler(executor)
      } else {
        executor()
      }
    })
}
