import { DeliverySpecificationCategory } from "./DeliverySpecificationCategory"
import { DeliverySpecificationId } from "./DeliverySpecification"
import {
  DeliverySpecificationAtSpecificationModification,
  DeliverySpecificationAtSpecificationModificationSchema,
} from "./DeliverySpecificationAtSpecificationModification"
import { makeJsonSchema, makeJsonSchemaValidate } from "../../../../infrastructure"

export type DeliverySpecificationAtSpecificationId = number

export interface DeliverySpecificationAtSpecification {
  id: DeliverySpecificationAtSpecificationId
  specificationId: DeliverySpecificationId
  childSpecificationId: DeliverySpecificationId
  category: DeliverySpecificationCategory // Раздел для работы
  modifications: Array<DeliverySpecificationAtSpecificationModification>
}

export const DeliverySpecificationAtSpecificationSchema = makeJsonSchema<DeliverySpecificationAtSpecification>({
  type: "object",
  properties: {
    id: { type: "number" },
    specificationId: { type: "number" },
    childSpecificationId: { type: "number" },
    category: { type: "number", enum: Object.values(DeliverySpecificationCategory) as Array<number> },
    modifications: { type: "array", items: DeliverySpecificationAtSpecificationModificationSchema },
  },
  required: [
    "id",
    "specificationId",
    "childSpecificationId",
    "category",
    "modifications",
  ],
})

export const DeliverySpecificationAtSpecificationValidator = makeJsonSchemaValidate<DeliverySpecificationAtSpecification>(DeliverySpecificationAtSpecificationSchema)
