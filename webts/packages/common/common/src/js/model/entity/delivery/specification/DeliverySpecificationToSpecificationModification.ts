import { DeliverySpecificationToSpecificationId } from "./DeliverySpecificationToSpecification"
import { makeJsonSchema, makeJsonSchemaValidate } from "../../../../infrastructure"

export type DeliverySpecificationToSpecificationModificationId = number

export interface DeliverySpecificationToSpecificationModification {
  id: DeliverySpecificationToSpecificationModificationId
  specificationToSpecificationId: DeliverySpecificationToSpecificationId
  number: number
  count: number
}

export const DeliverySpecificationToSpecificationModificationSchema = makeJsonSchema<DeliverySpecificationToSpecificationModification>({
  type: "object",
  properties: {
    id: { type: "number" },
    specificationToSpecificationId: { type: "number" },
    number: { type: "number" },
    count: { type: "number" },
  },
  required: [
    "id",
    "specificationToSpecificationId",
    "number",
    "count",
  ],
})

export const DeliverySpecificationToSpecificationModificationValidator = makeJsonSchemaValidate<DeliverySpecificationToSpecificationModification>(DeliverySpecificationToSpecificationModificationSchema)
