import { JsonSchema } from "./types"

export function makeJsonSchema<T>(schema: JsonSchema<T>): JsonSchema<T> {
  // eslint-disable-next-line @typescript-eslint/no-unsafe-return
  return {
    // additionalProperties: true,
    // propertyNames: {
    //   pattern: "^\\$[A-Za-z_][A-Za-z0-9_]$",
    // },
    // @ts-ignore
    ...schema,
  }
}
