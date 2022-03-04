export class LogLevel {
  static all = 0
  static trace = 20
  static debug = 40
  static info = 60
  static warn = 80
  static error = 100
  static wtf = 200
  static none = Number.MAX_VALUE

  static getName(level: number): string {
    const defaultLevel = (level: number): string => {
      switch (level) {
        case this.all:
          return "ALL"
        case this.trace:
          return "TRACE"
        case this.debug:
          return "DEBUG"
        case this.info:
          return "INFO"
        case this.warn:
          return "WARN"
        case this.error:
          return "ERROR"
        case this.wtf:
          return "WTF"
        default:
          return `level-${level}`
      }
    }
    return this.customNames[level] || defaultLevel(level)
  }

  static addCustomName(level: number, name: string): void {
    this.customNames[level] = name
  }

  private static customNames: Record<number, string> = {}
}
