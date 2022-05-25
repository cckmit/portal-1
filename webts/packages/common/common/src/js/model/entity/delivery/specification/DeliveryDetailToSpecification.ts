import { DeliveryDetailId } from "../detail"
import { DeliverySpecificationId } from "./DeliverySpecification"
import {
  DeliveryDetailToSpecificationModification,
  DeliveryDetailToSpecificationModificationSchema,
} from "./DeliveryDetailToSpecificationModification"
import { makeJsonSchema, makeJsonSchemaValidate } from "../../../../infrastructure"

export type DeliveryDetailToSpecificationId = number

export interface DeliveryDetailToSpecification {
  id: DeliveryDetailToSpecificationId
  specificationId: DeliverySpecificationId
  detailId: DeliveryDetailId
  dateModified: Date | undefined
  note: string | undefined
  partReference: string | undefined
  modifications: Array<DeliveryDetailToSpecificationModification>
}

export const DeliveryDetailToSpecificationSchema = makeJsonSchema<DeliveryDetailToSpecification>({
  type: "object",
  properties: {
    id: { type: "number" },
    specificationId: { type: "number" },
    detailId: { type: "number" },
    dateModified: { type: "object", format: "date-time", required: [], nullable: true },
    note: { type: "string", nullable: true },
    partReference: { type: "string", nullable: true },
    modifications: { type: "array", items: DeliveryDetailToSpecificationModificationSchema },
  },
  required: [
    "id",
    "specificationId",
    "detailId",
    "modifications",
  ],
})

export const DeliveryDetailToSpecificationValidator = makeJsonSchemaValidate<DeliveryDetailToSpecification>(DeliveryDetailToSpecificationSchema)
