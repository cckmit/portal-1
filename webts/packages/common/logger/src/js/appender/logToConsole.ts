import { LogLevel } from "@protei-libs/logger"

export function logToConsole(level: LogLevel, message: string): void {
  if (level >= LogLevel.wtf) {
    console.error(`%c${message}`, "color:#ff4141; background-color:#302b2b; padding:2px 3px;")
  } else if (level >= LogLevel.error) {
    console.error(message)
  } else if (level >= LogLevel.warn) {
    console.warn(message)
  } else if (level >= LogLevel.info) {
    console.info(message)
  } else if (level >= LogLevel.debug) {
    console.debug(message)
  } else if (level >= LogLevel.trace) {
    console.trace(message)
  } else {
    console.log(message)
  }
}
