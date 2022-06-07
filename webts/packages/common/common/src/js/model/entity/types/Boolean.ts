import { makeJsonSchemaValidate } from "../../../infrastructure"

export const BooleanValidator = makeJsonSchemaValidate<boolean>({
  type: "boolean",
})
