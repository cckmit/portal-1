import { Exception, exceptionToDescString, isException } from "@protei-libs/exception"
import { LogMessage, LogParameter } from "./LogMessage"

export function makeLogMessage(
  template: LogMessage,
  parameters: Array<LogParameter>,
  options: {
    json: JSON["stringify"]
    sensitiveKeys: Array<string>
  },
): [string, Exception | undefined] {
  const message = stringify(template, options.json, options.sensitiveKeys)
  if (parameters.length > 0) {
    const numberOfTokens = message.split(TOKEN).length - 1
    const numberOfParameters = parameters.length
    const lastParameter = parameters[numberOfParameters - 1]
    if (
      numberOfParameters > numberOfTokens &&
      (isException(lastParameter) || lastParameter instanceof Error)
    ) {
      const exception = parameters.pop() as Exception
      return [
        messageInsertParameters(message, parameters, options.json, options.sensitiveKeys),
        exception,
      ]
    }
  }
  return [
    messageInsertParameters(message, parameters, options.json, options.sensitiveKeys),
    undefined,
  ]
}

function messageInsertParameters(
  message: string,
  parameters: Array<LogParameter>,
  json: JSON["stringify"],
  sensitiveKeys: Array<string>,
): string {
  for (const parameter of parameters) {
    const param = stringify(parameter, json, sensitiveKeys)
    if (message.includes(TOKEN)) {
      message = message.replace(TOKEN, param)
    } else {
      message += param
    }
  }
  return message
}

function stringify(
  msg: LogMessage | LogParameter,
  json: JSON["stringify"],
  sensitiveKeys: Array<string>,
): string {
  if (msg === null) {
    return "<null>"
  } else if (typeof msg === "string") {
    return msg
  } else if (isException(msg)) {
    return exceptionToDescString(msg)
  } else if (msg instanceof Error) {
    return exceptionToDescString(msg)
  } else if (Array.isArray(msg)) {
    return "[" + msg.map((m) => stringify(m, json, sensitiveKeys)).join(",") + "]"
  } else if (msg instanceof Date) {
    return msg.toUTCString()
  } else if (typeof msg === "object") {
    return json(msg, sensitiveInfoReplacer(sensitiveKeys))
  } else if (typeof msg === "undefined") {
    return "<undefined>"
  } else {
    // unreachable, but everything could happen
    return json(msg, sensitiveInfoReplacer(sensitiveKeys))
  }
}

function sensitiveInfoReplacer(
  sensitiveKeys: Array<string>,
): (key: string, value: unknown) => unknown {
  return (key, value) => {
    // noinspection SuspiciousTypeOfGuard
    if (typeof key !== "string") {
      return value
    }
    if (sensitiveKeys.includes(key.toLowerCase())) {
      return "*****"
    }
    return value
  }
}

const TOKEN = "{}"
