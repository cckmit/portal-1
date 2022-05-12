import { inject, injectable } from "inversify"
import { PortalApiRequest, PortalApiTransport, PortalApiTransport$type } from "./core/PortalApiTransport"
import { CreateDeliverySpecification } from "../model"

export const DeliverySpecificationTransport$type = Symbol("DeliverySpecificationTransport")

export interface DeliverySpecificationTransport {
  create(request: CreateDeliverySpecification): Promise<void>
}

@injectable()
export class DeliverySpecificationTransportImpl implements DeliverySpecificationTransport {
  async create(request: CreateDeliverySpecification): Promise<void> {
    const req: PortalApiRequest = {
      method: "POST",
      url: "/delivery/specification",
      body: request,
    }
    const res = await this.transport.exchange(req)
  }

  constructor(
    @inject(PortalApiTransport$type) transport: PortalApiTransport,
  ) {
    this.transport = transport
  }

  private readonly transport: PortalApiTransport
}
