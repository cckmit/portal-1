import { DeliverySpecificationAtSpecificationId } from "./DeliverySpecificationAtSpecification"
import { makeJsonSchema, makeJsonSchemaValidate } from "../../../../infrastructure"

export type DeliverySpecificationAtSpecificationModificationId = number

export interface DeliverySpecificationAtSpecificationModification {
  id: DeliverySpecificationAtSpecificationModificationId
  specificationAtSpecificationId: DeliverySpecificationAtSpecificationId
  number: number
  count: number
}

export const DeliverySpecificationAtSpecificationModificationSchema = makeJsonSchema<DeliverySpecificationAtSpecificationModification>({
  type: "object",
  properties: {
    id: { type: "number" },
    specificationAtSpecificationId: { type: "number" },
    number: { type: "number" },
    count: { type: "number" },
  },
  required: [
    "id",
    "specificationAtSpecificationId",
    "number",
    "count",
  ],
})

export const DeliverySpecificationAtSpecificationModificationValidator = makeJsonSchemaValidate<DeliverySpecificationAtSpecificationModification>(DeliverySpecificationAtSpecificationModificationSchema)
