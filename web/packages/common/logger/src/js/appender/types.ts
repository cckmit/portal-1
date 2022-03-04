import { LogMessage, LogParameter } from "@protei-libs/logger"

export type LogDescriptor = {
  name: string
  time: Date
  level: number
  template: LogMessage
  parameters: Array<LogParameter>
}

export type LogHistory = {
  log: Array<string>
  maxLength: number
}
