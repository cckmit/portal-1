import { Logger } from "./Logger"
import { LogMessage, LogParameter } from "./LogMessage"
import { LogLevel } from "./LogLevel"
import { LoggerConfiguration } from "./LoggerConfiguration"

export function makeLogger(name: string): Logger {
  return {
    getName(): string {
      return name
    },
    trace(message: LogMessage, ...parameters: Array<LogParameter>) {
      log(name, new Date(), LogLevel.trace, message, ...parameters)
    },
    debug(message: LogMessage, ...parameters: Array<LogParameter>) {
      log(name, new Date(), LogLevel.debug, message, ...parameters)
    },
    info(message: LogMessage, ...parameters: Array<LogParameter>) {
      log(name, new Date(), LogLevel.info, message, ...parameters)
    },
    warn(message: LogMessage, ...parameters: Array<LogParameter>) {
      log(name, new Date(), LogLevel.warn, message, ...parameters)
    },
    error(message: LogMessage, ...parameters: Array<LogParameter>) {
      log(name, new Date(), LogLevel.error, message, ...parameters)
    },
    wtf(template: LogMessage, ...parameters: Array<LogParameter>) {
      log(name, new Date(), LogLevel.wtf, template, ...parameters)
    },
    log(level: number, message: LogMessage, ...parameters: Array<LogParameter>) {
      log(name, new Date(), level, message, ...parameters)
    },
  }
}

export function addLogConfiguration(configuration: LoggerConfiguration): void {
  configurations.push(configuration)
}

function log(
  name: string,
  time: Date,
  level: number,
  message: LogMessage,
  ...parameters: Array<LogParameter>
): void {
  configurations
    .filter((cfg) => !cfg.names || cfg.names.length === 0 || cfg.names.includes(name))
    .filter((cfg) => cfg.level <= level)
    .forEach((cfg) =>
      cfg.appenders.forEach((appender) => {
        appender(name, time, level, message, ...parameters)
      }),
    )
}

const configurations: Array<LoggerConfiguration> = []
