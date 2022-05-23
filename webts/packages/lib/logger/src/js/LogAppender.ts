import { LogMessage, LogParameter } from "./LogMessage"

export type LogAppender = (
  name: string,
  time: Date,
  level: number,
  template: LogMessage,
  ...parameters: Array<LogParameter>
) => void
