import { toJS } from "mobx"
import { LogMessage, LogParameter } from "@protei-libs/logger"
import { LogDescriptor, LogHistory } from "./types"
import { makeMessage } from "./message"
import { logToConsole } from "./logToConsole"
import { _logHistory, _logHistoryMaxLength, logToHistory } from "./logHistory"

export function portalLogAppender(
  name: string,
  time: Date,
  level: number,
  template: LogMessage,
  ...parameters: Array<LogParameter>
): void {
  const descriptor: LogDescriptor = {
    name: name,
    time: time,
    level: level,
    template: cleanLog(template),
    parameters: parameters.map(cleanLog),
  }
  const message = makeMessage(descriptor)
  logToConsole(level, message)
  logToHistory(level, message)
}

export function portalGetLogHistory(): Promise<LogHistory> {
  return new Promise((resolve) => {
    resolve({
      log: _logHistory(),
      maxLength: _logHistoryMaxLength(),
    })
  })
}

function cleanLog(log: LogMessage): LogMessage
function cleanLog(log: LogParameter): LogParameter
function cleanLog(log: LogMessage | LogParameter): LogMessage | LogParameter {
  if (toJS != undefined) {
    return toJS(log)
  }
  return log
}
