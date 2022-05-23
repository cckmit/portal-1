import { AnyFunction } from "@protei-libs/types"
import { Exception } from "./Exception"

export function newException(
  name: string,
  error?: {
    message?: string
    cause?: Error | unknown
    stack?: string
  },
  funcRef?: AnyFunction,
): Exception {
  const exception: Exception = {
    ...error,
    _tag: "Exception",
    name: name,
    message: error?.message || name,
  }
  if (!exception.stack && typeof Error.captureStackTrace === "function") {
    Error.captureStackTrace(exception, funcRef ?? newException)
  }
  return exception
}
