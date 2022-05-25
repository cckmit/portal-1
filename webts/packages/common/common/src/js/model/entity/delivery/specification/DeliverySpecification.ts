import { PersonId } from "../../person"
import { DeliveryDetailToSpecification, DeliveryDetailToSpecificationSchema } from "./DeliveryDetailToSpecification"
import {
  DeliverySpecificationToSpecification,
  DeliverySpecificationToSpecificationSchema,
} from "./DeliverySpecificationToSpecification"
import { makeJsonSchema, makeJsonSchemaValidate } from "../../../../infrastructure"

export type DeliverySpecificationId = number

export interface DeliverySpecification {
  id: DeliverySpecificationId
  creatorId: PersonId
  dateCreated: Date
  dateModified: Date
  name: string
  details: Array<DeliveryDetailToSpecification>
  specifications: Array<DeliverySpecificationToSpecification>
}

export const DeliverySpecificationSchema = makeJsonSchema<DeliverySpecification>({
  type: "object",
  properties: {
    id: { type: "number" },
    creatorId: { type: "number" },
    dateCreated: { type: "object", format: "date-time", required: [] },
    dateModified: { type: "object", format: "date-time", required: [] },
    name: { type: "string" },
    details: { type: "array", items: DeliveryDetailToSpecificationSchema },
    specifications: { type: "array", items: DeliverySpecificationToSpecificationSchema },
  },
  required: [
    "id",
    "creatorId",
    "dateCreated",
    "dateModified",
    "name",
    "details",
    "specifications",
  ],
})

export const DeliverySpecificationValidator = makeJsonSchemaValidate<DeliverySpecification>(DeliverySpecificationSchema)
