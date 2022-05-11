import { PersonId } from "../../person"
import { DeliveryDetailAtSpecification, DeliveryDetailAtSpecificationSchema } from "./DeliveryDetailAtSpecification"
import {
  DeliverySpecificationAtSpecification,
  DeliverySpecificationAtSpecificationSchema,
} from "./DeliverySpecificationAtSpecification"
import { makeJsonSchema, makeJsonSchemaValidate } from "../../../../infrastructure"

export type DeliverySpecificationId = number

export interface DeliverySpecification {
  id: DeliverySpecificationId
  creatorId: PersonId
  created: Date
  modified: Date
  name: string
  details: Array<DeliveryDetailAtSpecification>
  specifications: Array<DeliverySpecificationAtSpecification>
}

export const DeliverySpecificationSchema = makeJsonSchema<DeliverySpecification>({
  type: "object",
  properties: {
    id: { type: "number" },
    creatorId: { type: "number" },
    created: { type: "object", format: "date-time", required: [] },
    modified: { type: "object", format: "date-time", required: [] },
    name: { type: "string" },
    details: { type: "array", items: DeliveryDetailAtSpecificationSchema },
    specifications: { type: "array", items: DeliverySpecificationAtSpecificationSchema },
  },
  required: [
    "id",
    "creatorId",
    "created",
    "modified",
    "name",
    "details",
    "specifications",
  ],
})

export const DeliverySpecificationValidator = makeJsonSchemaValidate<DeliverySpecification>(DeliverySpecificationSchema)
