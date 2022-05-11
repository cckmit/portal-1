import { DeliverySpecificationId } from "./DeliverySpecification"
import { CreateDeliverySpecification } from "./CreateDeliverySpecification"
import { DeliverySpecificationCategory } from "./DeliverySpecificationCategory"
import {
  CreateDeliverySpecificationAtSpecificationModification,
} from "./CreateDeliverySpecificationAtSpecificationModification"

export interface CreateDeliverySpecificationAtSpecification {
  specification: CreateDeliverySpecification | undefined
  specificationId: DeliverySpecificationId | undefined
  category: DeliverySpecificationCategory // Раздел для работы
  modifications: Array<CreateDeliverySpecificationAtSpecificationModification>
}
