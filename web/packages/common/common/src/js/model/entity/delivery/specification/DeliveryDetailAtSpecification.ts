import { DeliveryDetailId } from "../detail"
import { DeliverySpecificationId } from "./DeliverySpecification"
import {
  DeliveryDetailAtSpecificationModification,
  DeliveryDetailAtSpecificationModificationSchema,
} from "./DeliveryDetailAtSpecificationModification"
import { makeJsonSchema, makeJsonSchemaValidate } from "../../../../infrastructure"

export type DeliveryDetailAtSpecificationId = number

export interface DeliveryDetailAtSpecification {
  id: DeliveryDetailAtSpecificationId
  specificationId: DeliverySpecificationId
  detailId: DeliveryDetailId
  modified: Date | undefined // Дата изменения поля
  note: string | undefined // Примечание
  partReference: string | undefined
  modifications: Array<DeliveryDetailAtSpecificationModification>
}

export const DeliveryDetailAtSpecificationSchema = makeJsonSchema<DeliveryDetailAtSpecification>({
  type: "object",
  properties: {
    id: { type: "number" },
    specificationId: { type: "number" },
    detailId: { type: "number" },
    modified: { type: "object", format: "date-time", required: [], nullable: true },
    note: { type: "string", nullable: true },
    partReference: { type: "string", nullable: true },
    modifications: { type: "array", items: DeliveryDetailAtSpecificationModificationSchema },
  },
  required: [
    "id",
    "specificationId",
    "detailId",
    "modifications",
  ],
})

export const DeliveryDetailAtSpecificationValidator = makeJsonSchemaValidate<DeliveryDetailAtSpecification>(DeliveryDetailAtSpecificationSchema)
