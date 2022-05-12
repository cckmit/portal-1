/* eslint-disable @typescript-eslint/ban-types */
import { En_ResultStatus } from "../En_ResultStatus"
import { makeJsonSchema, makeJsonSchemaValidate } from "../../../infrastructure"

export interface JsonResponse<T = unknown> {
  requestId: string
  status: En_ResultStatus
  data: T | undefined
  message: string | undefined
}

export const JsonResponseSchema = makeJsonSchema<JsonResponse<{}>>({
  type: "object",
  properties: {
    requestId: { type: "string" },
    status: { type: "string" },
    data: {
      type: "object",
      required: [],
      nullable: true,
    },
    message: { type: "string", nullable: true },
  },
  required: [
    "requestId",
    "status",
  ],
})

export const JsonResponseValidator = makeJsonSchemaValidate<JsonResponse<{}>>(JsonResponseSchema)
