import { PersonId } from "../../person"
import { CompanyId } from "../../company"
import { DeliverySpecificationCategory } from "../specification"
import { makeJsonSchema, makeJsonSchemaValidate } from "../../../../infrastructure"

export type DeliveryDetailId = number

export interface DeliveryDetail {
  id: DeliveryDetailId
  article: string | undefined
  name: string
  responsibleId: PersonId
  supplierId: CompanyId
  configuration: string | undefined
  color: string | undefined
  reserve: number | undefined
  category: DeliverySpecificationCategory
  simplified: boolean
  attn: boolean
  componentType: string | undefined
  value: string | undefined
}

export const DeliveryDetailSchema = makeJsonSchema<DeliveryDetail>({
  type: "object",
  properties: {
    id: { type: "number" },
    article: { type: "string", nullable: true },
    name: { type: "string" },
    responsibleId: { type: "number" },
    supplierId: { type: "number" },
    configuration: { type: "string", nullable: true },
    color: { type: "string", nullable: true },
    reserve: { type: "number", nullable: true },
    category: { type: "number", enum: Object.values(DeliverySpecificationCategory) as Array<number> },
    simplified: { type: "boolean" },
    attn: { type: "boolean" },
    componentType: { type: "string", nullable: true },
    value: { type: "string", nullable: true },
  },
  required: [
    "id",
    "name",
    "responsibleId",
    "supplierId",
    "category",
    "simplified",
    "attn",
  ],
})

export const DeliveryDetailValidator = makeJsonSchemaValidate<DeliveryDetail>(DeliveryDetailSchema)
