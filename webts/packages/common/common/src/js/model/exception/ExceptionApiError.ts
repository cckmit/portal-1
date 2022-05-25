import { AnyFunction } from "@protei-libs/types"
import { Exception, isException, newException } from "@protei-libs/exception"
import { ExceptionName } from "./ExceptionName"
import { En_ResultStatus } from "../entity"
import { isNotUndefined } from "../../util"

type ErrorDtoLike = {
  status: En_ResultStatus
  message?: string
}

export interface ExceptionApiError extends Exception {
  errorStatus: En_ResultStatus
  errorMessage?: string
}

export function newExceptionApiError(errorDto: ErrorDtoLike, funcRef?: AnyFunction): ExceptionApiError {
  const message = makeApiErrorMessage(errorDto)
  return {
    ...newException(ExceptionName.API_ERROR, { message }, funcRef ?? newExceptionApiError),
    errorStatus: errorDto.status,
    errorMessage: errorDto.message,
  }
}

// eslint-disable-next-line @typescript-eslint/no-explicit-any
export const isExceptionApiError = (entity: any | undefined): entity is ExceptionApiError => isException(entity) && entity.name === ExceptionName.API_ERROR && "errorStatus" in entity

function makeApiErrorMessage(errorDto: ErrorDtoLike, message?: string): string {
  const backendMessage = makeBackendMessage(errorDto)
  if (message && backendMessage) {
    return `${message} [${backendMessage}]`
  } else if (message) {
    return message
  } else {
    return backendMessage
  }
}

function makeBackendMessage(errorDto: ErrorDtoLike): string {
  return [errorDto.status, makeBackendMessageFull(errorDto)].filter(isNotUndefined).join(" ")
}

function makeBackendMessageFull(errorDto: ErrorDtoLike): string | undefined {
  if (!errorDto.message) {
    return undefined
  }
  const message = errorDto.message
  return `(${message})`
}
