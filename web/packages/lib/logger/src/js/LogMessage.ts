import { Exception } from "@protei-libs/exception"

export type LogMessage = string | LogToStringable
export type LogParameter =
  | LogMessage
  | Date
  | Array<LogMessage>
  | Record<string | number | symbol, LogMessage>
  | Exception
  | Error
  | undefined
  | null
  | (() => LogParameter)
export type LogToStringable = {
  toString(): string
}
