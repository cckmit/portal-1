import { DeliveryDetailToSpecificationId } from "./DeliveryDetailToSpecification"
import { makeJsonSchema, makeJsonSchemaValidate } from "../../../../infrastructure"

export type DeliveryDetailToSpecificationModificationId = number

export interface DeliveryDetailToSpecificationModification {
  id: DeliveryDetailToSpecificationModificationId
  detailToSpecificationId: DeliveryDetailToSpecificationId
  number: number
  count: number
}

export const DeliveryDetailToSpecificationModificationSchema = makeJsonSchema<DeliveryDetailToSpecificationModification>({
  type: "object",
  properties: {
    id: { type: "number" },
    detailToSpecificationId: { type: "number" },
    number: { type: "number" },
    count: { type: "number" },
  },
  required: [
    "id",
    "detailToSpecificationId",
    "number",
    "count",
  ],
})

export const DeliveryDetailToSpecificationModificationValidator = makeJsonSchemaValidate<DeliveryDetailToSpecificationModification>(DeliveryDetailToSpecificationModificationSchema)
