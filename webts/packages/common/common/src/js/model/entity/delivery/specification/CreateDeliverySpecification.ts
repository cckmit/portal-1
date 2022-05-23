import { CreateDeliveryDetailAtSpecification } from "./CreateDeliveryDetailAtSpecification"
import { CreateDeliverySpecificationAtSpecification } from "./CreateDeliverySpecificationAtSpecification"

export interface CreateDeliverySpecification {
  name: string
  details: Array<CreateDeliveryDetailAtSpecification>
  specifications: Array<CreateDeliverySpecificationAtSpecification>
}
