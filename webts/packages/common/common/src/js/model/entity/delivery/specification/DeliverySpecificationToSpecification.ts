import { DeliverySpecificationCategory } from "./DeliverySpecificationCategory"
import { DeliverySpecificationId } from "./DeliverySpecification"
import {
  DeliverySpecificationToSpecificationModification,
  DeliverySpecificationToSpecificationModificationSchema,
} from "./DeliverySpecificationToSpecificationModification"
import { makeJsonSchema, makeJsonSchemaValidate } from "../../../../infrastructure"

export type DeliverySpecificationToSpecificationId = number

export interface DeliverySpecificationToSpecification {
  id: DeliverySpecificationToSpecificationId
  specificationId: DeliverySpecificationId
  childSpecificationId: DeliverySpecificationId
  category: DeliverySpecificationCategory
  modifications: Array<DeliverySpecificationToSpecificationModification>
}

export const DeliverySpecificationToSpecificationSchema = makeJsonSchema<DeliverySpecificationToSpecification>({
  type: "object",
  properties: {
    id: { type: "number" },
    specificationId: { type: "number" },
    childSpecificationId: { type: "number" },
    category: { type: "number", enum: Object.values(DeliverySpecificationCategory) as Array<number> },
    modifications: { type: "array", items: DeliverySpecificationToSpecificationModificationSchema },
  },
  required: [
    "id",
    "specificationId",
    "childSpecificationId",
    "category",
    "modifications",
  ],
})

export const DeliverySpecificationToSpecificationValidator = makeJsonSchemaValidate<DeliverySpecificationToSpecification>(DeliverySpecificationToSpecificationSchema)
