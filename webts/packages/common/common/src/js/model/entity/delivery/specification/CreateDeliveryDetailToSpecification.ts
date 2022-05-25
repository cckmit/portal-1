import { CreateDeliveryDetail, DeliveryDetailId } from "../detail"
import { CreateDeliveryDetailToSpecificationModification } from "./CreateDeliveryDetailToSpecificationModification"

export interface CreateDeliveryDetailToSpecification {
  detail: CreateDeliveryDetail | undefined
  detailId: DeliveryDetailId | undefined
  dateModified: Date | undefined
  note: string | undefined
  partReference: string | undefined
  modifications: Array<CreateDeliveryDetailToSpecificationModification>
}
