import { DeliveryDetailToSpecificationModification } from "./DeliveryDetailToSpecificationModification"

export type CreateDeliveryDetailToSpecificationModification =
  Omit<DeliveryDetailToSpecificationModification, "id" | "detailToSpecificationId">
