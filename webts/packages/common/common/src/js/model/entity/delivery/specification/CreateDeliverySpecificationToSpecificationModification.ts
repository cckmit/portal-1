import { DeliverySpecificationToSpecificationModification } from "./DeliverySpecificationToSpecificationModification"

export type CreateDeliverySpecificationToSpecificationModification =
  Omit<DeliverySpecificationToSpecificationModification, "id" | "specificationToSpecificationId">
