import { LogAppender } from "./LogAppender"

export interface LoggerConfiguration {
  level: number
  names?: Array<string>
  appenders: Array<LogAppender>
}
