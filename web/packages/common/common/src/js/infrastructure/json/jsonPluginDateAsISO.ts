import { JsonPlugin } from "./json"
import { dateToIso, tsOrIsoToDate } from "../../util"

export function jsonPluginDateAsISO(): JsonPlugin {
  return {
    parse: (key: string, value: unknown): unknown => {
      if (value === null || value === undefined) {
        return undefined
      }
      if (key.startsWith("date") && (typeof value === "number" || typeof value === "string")) {
        try {
          return tsOrIsoToDate(value)
        } catch (e) {
          return value
        }
      }
      return value
    },
    stringify: (key: string, value: unknown): unknown => {
      if (value === null || value === undefined) {
        return value
      }
      if (value instanceof Date) {
        return dateToIso(value)
      }
      return value
    },
  }
}
