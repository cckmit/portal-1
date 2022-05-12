import { injectable } from "inversify"

export const PortalApiRequestIdProvider$type = Symbol("PortalApiRequestIdProvider")

export interface PortalApiRequestIdProvider {
  next(): string
}

@injectable()
export class PortalApiRequestIdProviderImpl implements PortalApiRequestIdProvider {

  next(): string {
    const id = this.requestId++
    return id.toFixed()
  }

  constructor() {
  }

  private requestId = 0
}
