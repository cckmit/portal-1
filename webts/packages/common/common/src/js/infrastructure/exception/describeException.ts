import { Exception, exceptionToDescString, exceptionToShortString, isExceptionNamed } from "@protei-libs/exception"
import { ExceptionName, isExceptionApiError } from "../../model"

export function describeException(exception: Exception): {
  header: string
  description: string | undefined
} {
  if (isExceptionApiError(exception)) {
    const header = t("api-error")
    const description = exceptionToDescString(exception)
    return { header, description }
  }
  if (isExceptionNamed(exception, ExceptionName.NATIVE)) {
    const header = t("native")
    const description = exceptionToDescString(exception)
    return { header, description }
  }
  if (isExceptionNamed(exception, ExceptionName.REACT)) {
    const header = t("react")
    const description = exceptionToShortString(exception)
    return { header, description }
  }
  if (isExceptionNamed(exception, ExceptionName.UNAVAILABLE)) {
    const header = t("unavailable")
    const description = undefined
    return { header, description }
  }
  if (isExceptionNamed(exception, ExceptionName.API_OFFLINE)) {
    const header = t("api-offline")
    const description = undefined
    return { header, description }
  }
  if (isExceptionNamed(exception, ExceptionName.API_DISCONNECTED)) {
    const header = t("api-disconnected")
    const description = undefined
    return { header, description }
  }
  if (isExceptionNamed(exception, ExceptionName.API_TIMEOUT)) {
    const header = t("api-timeout")
    const description = undefined
    return { header, description }
  }
  if (isExceptionNamed(exception, ExceptionName.API_PARSE)) {
    const header = t("api-parse")
    const description = exceptionToDescString(exception)
    return { header, description }
  }

  const header = t("any")
  const description = exceptionToDescString(exception)
  return { header, description }
}

// Should be lang
function t(key: string): string {
  return key
}
