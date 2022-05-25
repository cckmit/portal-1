import { LogLevel, makeLogMessage } from "@protei-libs/logger"
import { exceptionToString } from "@protei-libs/exception"
import { LogDescriptor } from "./types"

export function makeMessage(descriptor: LogDescriptor): string {
  const [message, error] = makeLogMessage(descriptor.template, descriptor.parameters, {
    json: JSON.stringify,
    sensitiveKeys: ["password", "authtoken", "token"],
  })
  const level = LogLevel.getName(descriptor.level)
  const time = timeRepresentation(descriptor.time)
  if (error) {
    return `${time} ${level} [${descriptor.name}] - ${message}\n${exceptionToString(error)}`
  } else {
    return `${time} ${level} [${descriptor.name}] - ${message}`
  }
}

function timeRepresentation(time: Date): string {
  const hours = time.getHours()
  const minutes = time.getMinutes()
  const seconds = time.getSeconds()
  const milliseconds = time.getMilliseconds()
  return (
    String(hours).padStart(2, "0") +
    ":" +
    String(minutes).padStart(2, "0") +
    ":" +
    String(seconds).padStart(2, "0") +
    "." +
    String(milliseconds).padStart(3, "0")
  )
}
