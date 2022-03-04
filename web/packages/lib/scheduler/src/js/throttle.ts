/* eslint-disable @typescript-eslint/no-explicit-any */
import { AnyToVoidFunction, ParametersToVoidFunction } from "@protei-libs/types"

export function throttle<F extends AnyToVoidFunction>(
  ms: number,
  fn: F,
  shouldRunFirst: boolean = false,
): ParametersToVoidFunction<F> {
  let interval: number | undefined
  let isPending: boolean
  let args: Parameters<F>
  return (..._args: Parameters<F>) => {
    isPending = true
    args = _args
    if (!interval) {
      if (shouldRunFirst) {
        isPending = false
        fn(...(args as any))
      }
      interval = self.setInterval(() => {
        if (!isPending) {
          interval && self.clearInterval(interval)
          interval = undefined
          return
        }
        isPending = false
        fn(...(args as any))
      }, ms)
    }
  }
}
