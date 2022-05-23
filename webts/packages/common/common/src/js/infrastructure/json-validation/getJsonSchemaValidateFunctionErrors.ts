import { ajv } from "./ajv"
import { JsonSchemaValidateFunction } from "./types"

export function getJsonSchemaValidateFunctionErrors(validate: JsonSchemaValidateFunction): string {
  const separator = validate.errors != null && validate.errors.length > 10 ? "\n" : ", "
  return ajv.errorsText(validate.errors, { separator })
}
