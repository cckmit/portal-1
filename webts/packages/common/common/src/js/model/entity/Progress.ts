import { Exception } from "@protei-libs/exception"

export type Progress = ProgressProcessing | ProgressReady | ProgressError

export type ProgressProcessing = {
  readonly _tag: "ProgressProcessing"
  readonly attempt: number
}
export const isProgressProcessing = (p: Progress): p is ProgressProcessing => p._tag === "ProgressProcessing"
export function progressProcessing(attempt = 1): ProgressProcessing {
  return {
    _tag: "ProgressProcessing",
    attempt: attempt,
  }
}

export type ProgressReady = {
  readonly _tag: "ProgressReady"
}
export const isProgressReady = (p: Progress): p is ProgressReady => p._tag === "ProgressReady"
export function progressReady(): ProgressReady {
  return {
    _tag: "ProgressReady",
  }
}

export type ProgressError = {
  readonly _tag: "ProgressError"
  readonly exception: Exception
}
export const isProgressError = (p: Progress): p is ProgressError => p._tag === "ProgressError"
export function progressError(exception: Exception): ProgressError {
  return {
    _tag: "ProgressError",
    exception: exception,
  }
}
