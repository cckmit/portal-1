import { DeliveryDetailAtSpecificationId } from "./DeliveryDetailAtSpecification"
import { makeJsonSchema, makeJsonSchemaValidate } from "../../../../infrastructure"

export type DeliveryDetailAtSpecificationModificationId = number

export interface DeliveryDetailAtSpecificationModification {
  id: DeliveryDetailAtSpecificationModificationId
  detailAtSpecificationId: DeliveryDetailAtSpecificationId
  number: number
  count: number
}

export const DeliveryDetailAtSpecificationModificationSchema = makeJsonSchema<DeliveryDetailAtSpecificationModification>({
  type: "object",
  properties: {
    id: { type: "number" },
    detailAtSpecificationId: { type: "number" },
    number: { type: "number" },
    count: { type: "number" },
  },
  required: [
    "id",
    "detailAtSpecificationId",
    "number",
    "count",
  ],
})

export const DeliveryDetailAtSpecificationModificationValidator = makeJsonSchemaValidate<DeliveryDetailAtSpecificationModification>(DeliveryDetailAtSpecificationModificationSchema)
