import { DeliveryDetail, DeliveryDetailId } from "./DeliveryDetail"

export type CreateDeliveryDetail = {
  id?: DeliveryDetailId
} & Omit<DeliveryDetail, "id">
