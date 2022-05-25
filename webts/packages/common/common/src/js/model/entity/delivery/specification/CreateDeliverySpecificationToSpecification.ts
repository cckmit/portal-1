import { DeliverySpecificationId } from "./DeliverySpecification"
import { CreateDeliverySpecification } from "./CreateDeliverySpecification"
import { DeliverySpecificationCategory } from "./DeliverySpecificationCategory"
import {
  CreateDeliverySpecificationToSpecificationModification,
} from "./CreateDeliverySpecificationToSpecificationModification"

export interface CreateDeliverySpecificationToSpecification {
  specification: CreateDeliverySpecification | undefined
  specificationId: DeliverySpecificationId | undefined
  category: DeliverySpecificationCategory
  modifications: Array<CreateDeliverySpecificationToSpecificationModification>
}
