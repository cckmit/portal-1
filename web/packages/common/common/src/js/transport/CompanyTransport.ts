import { inject, injectable } from "inversify"
import { PortalApiRequest, PortalApiTransport, PortalApiTransport$type } from "./core/PortalApiTransport"
import { ArrayValidator, CompanyQuery, EntityOption, EntityOptionSchema } from "../model"
import { validateApiResponse } from "../infrastructure"

export const CompanyTransport$type = Symbol("CompanyTransport")

export interface CompanyTransport {
  getCompanyOptionListByQuery(query: CompanyQuery): Promise<Array<EntityOption>>
}

@injectable()
export class CompanyTransportImpl implements CompanyTransport {
  async getCompanyOptionListByQuery(query: CompanyQuery): Promise<Array<EntityOption>> {
    const req: PortalApiRequest = {
      method: "POST",
      url: "/company/getCompanyOptionListByQuery",
      body: query,
    }
    const res = await this.transport.exchange(req)
    return validateApiResponse(res.body, ArrayValidator(EntityOptionSchema))
  }

  constructor(
    @inject(PortalApiTransport$type) transport: PortalApiTransport,
  ) {
    this.transport = transport
  }

  private readonly transport: PortalApiTransport
}
