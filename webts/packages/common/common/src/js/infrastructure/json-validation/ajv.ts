import Ajv from "ajv"
import { DEV } from "../../globals"

export const ajv = new Ajv({
  validateSchema: DEV,
  allErrors: true,
  removeAdditional: true,
  code: {
    lines: true,
  },
})
