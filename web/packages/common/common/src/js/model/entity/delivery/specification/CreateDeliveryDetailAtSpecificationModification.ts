import { DeliveryDetailAtSpecificationModification } from "./DeliveryDetailAtSpecificationModification"

export type CreateDeliveryDetailAtSpecificationModification =
  Omit<DeliveryDetailAtSpecificationModification, "id" | "detailAtSpecificationId">
