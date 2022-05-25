import { CreateDeliveryDetailToSpecification } from "./CreateDeliveryDetailToSpecification"
import { CreateDeliverySpecificationToSpecification } from "./CreateDeliverySpecificationToSpecification"

export interface CreateDeliverySpecification {
  name: string
  details: Array<CreateDeliveryDetailToSpecification>
  specifications: Array<CreateDeliverySpecificationToSpecification>
}
