import { inject, injectable } from "inversify"
import { PortalApiRequest, PortalApiTransport, PortalApiTransport$type } from "./core/PortalApiTransport"
import { CreateDeliverySpecification, DeliverySpecification, DeliverySpecificationValidator } from "../model"
import { validateApiResponse } from "../infrastructure"

export const DeliverySpecificationTransport$type = Symbol("DeliverySpecificationTransport")

export interface DeliverySpecificationTransport {
  create(request: CreateDeliverySpecification): Promise<DeliverySpecification>
}

@injectable()
export class DeliverySpecificationTransportImpl implements DeliverySpecificationTransport {
  async create(request: CreateDeliverySpecification): Promise<DeliverySpecification> {
    const req: PortalApiRequest = {
      method: "POST",
      url: "/deliverySpecification/createDeliverySpecification",
      body: request,
    }
    const res = await this.transport.exchange(req)
    return validateApiResponse(res.body, DeliverySpecificationValidator)
  }

  constructor(
    @inject(PortalApiTransport$type) transport: PortalApiTransport,
  ) {
    this.transport = transport
  }

  private readonly transport: PortalApiTransport
}
