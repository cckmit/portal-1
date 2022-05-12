import { PersonId } from "../../person"
import { CompanyId } from "../../company"
import { DeliverySpecificationCategory } from "../specification"
import { makeJsonSchema, makeJsonSchemaValidate } from "../../../../infrastructure"

export type DeliveryDetailId = number

export interface DeliveryDetail {
  id: DeliveryDetailId
  article: string | undefined // Артикул
  name: string // Наименование
  responsibleId: PersonId // Ответственный
  supplierId: CompanyId // Поставщик
  configuration: string | undefined // Конфигурация
  color: string | undefined // Цвет
  reserve: number | undefined // Технологический запас, %
  category: DeliverySpecificationCategory // Раздел для работы
  simplified: boolean // Метка попадания в упрощенную спецификацию
  attn: boolean | undefined // Признак
  componentType: string | undefined // Тип компоненты
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
    attn: { type: "boolean", nullable: true },
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
  ],
})

export const DeliveryDetailValidator = makeJsonSchemaValidate<DeliveryDetail>(DeliveryDetailSchema)
