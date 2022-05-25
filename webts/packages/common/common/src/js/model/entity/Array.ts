import { JsonSchema, JsonSchemaValidate, makeJsonSchema, makeJsonSchemaValidate } from "../../infrastructure"

export function ArrayValidator<T>(schema: JsonSchema<T>): JsonSchemaValidate<Array<T>> {
  const ArraySchema = makeJsonSchema<Array<T>>({
    type: "array",
    items: schema,
  })
  return makeJsonSchemaValidate<Array<T>>(ArraySchema)
}
