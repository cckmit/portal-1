import { Exception, exceptionToDescString, exceptionToShortString, isExceptionNamed } from "@protei-libs/exception"
import { ExceptionName } from "../../model"
import { getLang } from "@protei-portal/common-lang"

export function describeException(exception: Exception): {
  header: string
  description: string | undefined
} {
  const lang = getLang()
  if (isExceptionNamed(exception, ExceptionName.NATIVE)) {
    const header = lang.exceptionNative()
    const description = exceptionToShortString(exception)
    return { header, description }
  }
  if (isExceptionNamed(exception, ExceptionName.REACT)) {
    const header = lang.exceptionReact()
    const description = exceptionToShortString(exception)
    return { header, description }
  }
  if (isExceptionNamed(exception, ExceptionName.UNAVAILABLE)) {
    const header = lang.exceptionUnavailable()
    const description = undefined
    return { header, description }
  }
  if (isExceptionNamed(exception, ExceptionName.API_ERROR)) {
    const header = lang.exceptionApiError()
    const description = exceptionToShortString(exception)
    return { header, description }
  }
  if (isExceptionNamed(exception, ExceptionName.API_OFFLINE)) {
    const header = lang.exceptionApiOffline()
    const description = undefined
    return { header, description }
  }
  if (isExceptionNamed(exception, ExceptionName.API_TIMEOUT)) {
    const header = lang.exceptionApiTimeout()
    const description = undefined
    return { header, description }
  }
  if (isExceptionNamed(exception, ExceptionName.API_PARSE)) {
    const header = lang.exceptionApiParse()
    const description = exceptionToShortString(exception)
    return { header, description }
  }
  if (isExceptionNamed(exception, ExceptionName.API)) {
    const header = lang.exceptionApi()
    const description = exceptionToShortString(exception)
    return { header, description }
  }
  if (isExceptionNamed(exception, ExceptionName.IMPORT_XLSX_PARSE)) {
    const header = lang.exceptionImportXlsxParse()
    const description = exceptionToShortString(exception)
    return { header, description }
  }

  const header = lang.exceptionAny()
  const description = exceptionToDescString(exception)
  return { header, description }
}
