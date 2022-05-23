import { LogMessage, LogParameter } from "./LogMessage"

export interface Logger {
  getName(): string

  log(level: number, template: LogMessage, ...parameters: Array<LogParameter>): void

  trace(template: LogMessage, ...parameters: Array<LogParameter>): void

  debug(template: LogMessage, ...parameters: Array<LogParameter>): void

  info(template: LogMessage, ...parameters: Array<LogParameter>): void

  warn(template: LogMessage, ...parameters: Array<LogParameter>): void

  error(template: LogMessage, ...parameters: Array<LogParameter>): void

  // What a Terrible Failure: Report a condition that should never happen
  wtf(template: LogMessage, ...parameters: Array<LogParameter>): void
}
