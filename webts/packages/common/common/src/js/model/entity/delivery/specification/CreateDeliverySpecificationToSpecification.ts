import { DeliverySpecificationId } from "./DeliverySpecification"
import { DeliverySpecificationCategory } from "./DeliverySpecificationCategory"
import { CreateDeliverySpecificationToSpecificationModification } from "./CreateDeliverySpecificationToSpecificationModification"

export interface CreateDeliverySpecificationToSpecification {
  childSpecificationId: DeliverySpecificationId
  category: DeliverySpecificationCategory
  modifications: Array<CreateDeliverySpecificationToSpecificationModification>
}
