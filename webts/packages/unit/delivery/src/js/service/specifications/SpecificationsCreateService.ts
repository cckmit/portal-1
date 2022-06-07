import { inject, injectable } from "inversify"
import { makeLogger } from "@protei-libs/logger"
import { runInTransaction } from "@protei-libs/store"
import { DeliverySpecificationTransport, DeliverySpecificationTransport$type } from "@protei-portal/common"
import { SpecificationsCreateStore, SpecificationsCreateStore$type } from "../../store"

export const SpecificationsCreateService$type = Symbol("SpecificationsCreateService")

export interface SpecificationsCreateService {
  reset(opt?: { keepName?: boolean }): void
}

@injectable()
export class SpecificationsCreateServiceImpl implements SpecificationsCreateService {

  reset(opt?: { keepName?: boolean }): void {
    this.log.info("Reset | keepName={}", opt?.keepName)
    runInTransaction(() => {
      if (!this.specificationsCreateStore.specification) {
        return
      }
      if (opt?.keepName !== true) {
        this.specificationsCreateStore.specification.name = ""
      }
      this.specificationsCreateStore.specification.details = []
      this.specificationsCreateStore.specification.specifications = []
    })
  }

  constructor(
    @inject(SpecificationsCreateStore$type) specificationsCreateStore: SpecificationsCreateStore,
    @inject(DeliverySpecificationTransport$type) deliverySpecificationTransport: DeliverySpecificationTransport,
  ) {
    this.specificationsCreateStore = specificationsCreateStore
    this.deliverySpecificationTransport = deliverySpecificationTransport
  }

  private readonly specificationsCreateStore: SpecificationsCreateStore
  private readonly deliverySpecificationTransport: DeliverySpecificationTransport
  private readonly log = makeLogger("portal.delivery.spec.create")
}
