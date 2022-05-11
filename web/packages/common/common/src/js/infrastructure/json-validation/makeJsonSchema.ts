import { JsonSchema } from "./types"

export function makeJsonSchema<T>(schema: JsonSchema<T>): JsonSchema<T> {
  return {
    // additionalProperties: true,
    // propertyNames: {
    //   pattern: "^\\$[A-Za-z_][A-Za-z0-9_]$",
    // },
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    // @ts-ignore
    ...schema,
  }
}
