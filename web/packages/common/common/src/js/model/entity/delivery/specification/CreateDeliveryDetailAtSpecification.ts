import { CreateDeliveryDetail, DeliveryDetailId } from "../detail"
import { CreateDeliveryDetailAtSpecificationModification } from "./CreateDeliveryDetailAtSpecificationModification"

export interface CreateDeliveryDetailAtSpecification {
  detail: CreateDeliveryDetail | undefined
  detailId: DeliveryDetailId | undefined
  note: string | undefined // Примечание
  partReference: string | undefined
  modifications: Array<CreateDeliveryDetailAtSpecificationModification>
}
