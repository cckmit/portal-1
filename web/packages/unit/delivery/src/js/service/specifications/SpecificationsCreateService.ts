import { inject, injectable } from "inversify"
import { makeLogger } from "@protei-libs/logger"
import { runInTransaction } from "@protei-libs/store"
import {
  CreateDeliverySpecification,
  DeliverySpecificationTransport,
  DeliverySpecificationTransport$type,
  detectException,
  progressError,
  progressProcessing,
  progressReady,
} from "@protei-portal/common"
import { SpecificationsCreateStore, SpecificationsCreateStore$type } from "../../store"

export const SpecificationsCreateService$type = Symbol("SpecificationsCreateService")

export interface SpecificationsCreateService {
  reset(opt?: { keepName?: boolean }): void
  setName(name: string): void
  create(): Promise<void>
}

@injectable()
export class SpecificationsCreateServiceImpl implements SpecificationsCreateService {

  reset(opt?: { keepName?: boolean }): void {
    this.log.info("Reset")
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

  setName(name: string): void {
    this.log.info("Set name as '{}'", name)
    runInTransaction(() => {
      if (!this.specificationsCreateStore.specification) {
        this.specificationsCreateStore.specification = this.makeEmptySpecification()
      }
      this.specificationsCreateStore.specification.name = name
    })
  }

  async create(): Promise<void> {
    this.log.info("Create")
    const specification = this.specificationsCreateStore.specification
    if (specification === undefined) {
      this.log.warn("Unable to create specification, no specification defined")
      return
    }
    try {
      runInTransaction(() => {
        this.specificationsCreateStore.progress = progressProcessing()
      })
      const created = await this.deliverySpecificationTransport.create(specification)
      this.log.info("Create | done | specificationId={}", created.id)
      runInTransaction(() => {
        this.reset()
        this.specificationsCreateStore.progress = progressReady()
      })
    } catch (e) {
      const exception = detectException(e)
      this.log.error("Failed to create specification", exception)
      runInTransaction(() => {
        this.specificationsCreateStore.progress = progressError(exception)
      })
      throw exception
    }
  }

  private makeEmptySpecification(): CreateDeliverySpecification {
    return {
      name: "",
      specifications: [],
      details: [],
    }
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
