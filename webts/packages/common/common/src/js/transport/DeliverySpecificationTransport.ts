import { inject, injectable } from "inversify"
import { PortalApiRequest, PortalApiTransport, PortalApiTransport$type } from "./core/PortalApiTransport"
import { BooleanValidator, CreateDeliveryDetail, CreateDeliverySpecification } from "../model"
import { validateApiResponse } from "../infrastructure"

export const DeliverySpecificationTransport$type = Symbol("DeliverySpecificationTransport")

export interface DeliverySpecificationTransport {
  import(specifications: Array<CreateDeliverySpecification>, details: Array<CreateDeliveryDetail>): Promise<boolean>
  create(specifications: Array<CreateDeliverySpecification>): Promise<boolean>
}

@injectable()
export class DeliverySpecificationTransportImpl implements DeliverySpecificationTransport {
  async import(specifications: Array<CreateDeliverySpecification>, details: Array<CreateDeliveryDetail>): Promise<boolean> {
    const req: PortalApiRequest = {
      method: "POST",
      url: "/deliverySpecification/importDeliverySpecifications",
      body: {
        specifications: specifications,
        details: details,
      },
    }
    const res = await this.transport.exchange(req)
    return validateApiResponse(res.body, BooleanValidator)
  }

  async create(specifications: Array<CreateDeliverySpecification>): Promise<boolean> {
    const req: PortalApiRequest = {
      method: "POST",
      url: "/deliverySpecification/createDeliverySpecification",
      body: specifications,
    }
    const res = await this.transport.exchange(req)
    return validateApiResponse(res.body, BooleanValidator)
  }

  constructor(
    @inject(PortalApiTransport$type) transport: PortalApiTransport,
  ) {
    this.transport = transport
  }

  private readonly transport: PortalApiTransport
}
