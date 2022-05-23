import { Exception, makeException } from "@protei-libs/exception"
import { isExceptionApiError } from "../../model"

export function detectException(error: Error | unknown): Exception {
  const exception = makeException(error, detectException)
  if (isExceptionApiError(exception)) {
    // place to unified api exception detection
  }
  // TODO place to unified exception detection
  return exception
}
