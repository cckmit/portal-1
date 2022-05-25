import { newException } from "@protei-libs/exception"
import { JsonSchemaValidate } from "./types"
import { getJsonSchemaValidateFunctionErrors } from "./getJsonSchemaValidateFunctionErrors"
import { ExceptionName } from "../../model"

export function validateApiResponse<T>(dto: unknown, Validator: JsonSchemaValidate<T>): T {
  const validator = Validator()
  if (!validator(dto)) {
    const errors = getJsonSchemaValidateFunctionErrors(validator)
    const message = `Failed to parse api response: '${errors}'`
    throw newException(ExceptionName.API_PARSE, { message })
  }
  return dto
}
