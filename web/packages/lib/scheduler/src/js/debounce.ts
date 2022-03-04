/* eslint-disable @typescript-eslint/no-explicit-any */
import { AnyToVoidFunction, ParametersToVoidFunction } from "@protei-libs/types"

export function debounce<F extends AnyToVoidFunction>(
  ms: number,
  fn: F,
  shouldRunFirst: boolean = false,
  shouldRunLast: boolean = true,
): ParametersToVoidFunction<F> {
  let waitingTimeout: number | undefined
  return (...args: Parameters<F>) => {
    if (waitingTimeout) {
      self.clearTimeout(waitingTimeout)
      waitingTimeout = undefined
    } else if (shouldRunFirst) {
      fn(...(args as any))
    }
    waitingTimeout = self.setTimeout(() => {
      if (shouldRunLast) {
        fn(...(args as any))
      }
      waitingTimeout = undefined
    }, ms)
  }
}
