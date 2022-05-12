import { JsonPlugin } from "./json"

export function jsonPluginOmitNulls(): JsonPlugin {
  return {
    parse: (key: string, value: unknown): unknown => {
      if (value === null) {
        return undefined
      }
      return value
    },
    stringify: (key: string, value: unknown): unknown => {
      if (value === null) {
        return undefined
      }
      return value
    },
  }
}
