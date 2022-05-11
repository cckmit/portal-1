/* eslint-disable @typescript-eslint/no-explicit-any */
import Ajv, { JSONSchemaType } from "ajv"
import { DataValidationCxt, ErrorObject } from "ajv/dist/types"

export type JsonSchema<T> = JSONSchemaType<T>

export type JsonSchemaValidate<T = unknown> = () => JsonSchemaValidateFunction<T>

export interface JsonSchemaValidateFunction<T = unknown> {
  (this: Ajv | any, data: any, dataCxt?: DataValidationCxt): data is T

  errors?: null | ErrorObject[]
}
