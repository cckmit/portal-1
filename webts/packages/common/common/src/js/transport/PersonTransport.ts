import { inject, injectable } from "inversify"
import { PortalApiRequest, PortalApiTransport, PortalApiTransport$type } from "./core/PortalApiTransport"
import { ArrayValidator, EmployeeQuery, PersonShortView, PersonShortViewSchema } from "../model"
import { validateApiResponse } from "../infrastructure"

export const PersonTransport$type = Symbol("PersonTransport")

export interface PersonTransport {
  getPersonShortViewListByQuery(query: EmployeeQuery): Promise<Array<PersonShortView>>
}

@injectable()
export class PersonTransportImpl implements PersonTransport {
  async getPersonShortViewListByQuery(query: EmployeeQuery): Promise<Array<PersonShortView>> {
    const req: PortalApiRequest = {
      method: "POST",
      url: "/person/getPersonShortViewListByQuery",
      body: query,
    }
    const res = await this.transport.exchange(req)
    return validateApiResponse(res.body, ArrayValidator(PersonShortViewSchema))
  }

  constructor(
    @inject(PortalApiTransport$type) transport: PortalApiTransport,
  ) {
    this.transport = transport
  }

  private readonly transport: PortalApiTransport
}
