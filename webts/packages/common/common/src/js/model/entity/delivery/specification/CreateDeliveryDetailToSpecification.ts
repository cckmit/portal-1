import { DeliveryDetailId } from "../detail"
import { CreateDeliveryDetailToSpecificationModification } from "./CreateDeliveryDetailToSpecificationModification"

export interface CreateDeliveryDetailToSpecification {
  detailId: DeliveryDetailId
  dateModified: Date | undefined
  note: string | undefined
  partReference: string | undefined
  modifications: Array<CreateDeliveryDetailToSpecificationModification>
}
