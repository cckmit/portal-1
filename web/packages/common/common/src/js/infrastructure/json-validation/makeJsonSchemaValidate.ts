import { ajv } from "./ajv"
import { JsonSchema, JsonSchemaValidate } from "./types"

export function makeJsonSchemaValidate<T>(schema: JsonSchema<T>): JsonSchemaValidate<T> {
  const key = generateUniqueKey()
  ajv.addSchema(schema, key)
  return () => {
    // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
    return ajv.getSchema<T>(key)!
  }
}

function generateUniqueKey(): string {
  let key: string
  while (true) {
    key = Math.random().toString(36).substring(2, 15)
    if (ajv.schemas[key] === undefined) {
      break
    }
  }
  return key
}
