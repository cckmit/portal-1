import { PersonId } from "../../person"
import { CompanyId } from "../../company"
import { DeliverySpecificationCategory } from "../specification"

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
