import { AnyFunction } from "@protei-libs/types"
import { Exception } from "./Exception"
import { isException } from "./isException"
import { newException } from "./newException"

export function makeException(error: Error | unknown, funcRef?: AnyFunction): Exception {
  if (isException(error)) {
    return error
  }
  return makeExceptionNative(error, funcRef ?? makeException)
}

// eslint-disable-next-line @typescript-eslint/no-explicit-any
export function makeExceptionNative(error: Error | any, funcRef?: AnyFunction): Exception {
  return newException(
    "NATIVE",
    {
      message: `${error?.name}: ${error?.message}`,
      stack: error?.stack,
    },
    funcRef ?? makeExceptionNative,
  )
}
