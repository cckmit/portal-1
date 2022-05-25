import Ajv from "ajv"
import addFormats from "ajv-formats"
import { DEV } from "../../globals"

// eslint-disable-next-line @typescript-eslint/no-unsafe-call,@typescript-eslint/no-unsafe-assignment
export const ajv: Ajv = addFormats(new Ajv({
  validateSchema: DEV,
  allErrors: true,
  removeAdditional: true,
  code: {
    lines: true,
  },
}))
