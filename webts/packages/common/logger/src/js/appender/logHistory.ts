import { LogLevel } from "@protei-libs/logger"

const logHistory: Array<string> = []
const maxHistoryLength = 5000

export function logToHistory(level: LogLevel, message: string): void {
  logHistory.push(message + "\n")
  pruneHistory()
}

function pruneHistory(): void {
  const length = logHistory.length
  const overflow = length - maxHistoryLength
  if (overflow > 0) {
    logHistory.splice(0, overflow)
  }
}

export function _logHistory(): Array<string> {
  return logHistory
}

export function _logHistoryMaxLength(): number {
  return maxHistoryLength
}
