import { inject, injectable } from "inversify"
import { makeLogger } from "@protei-libs/logger"
import { runInTransaction } from "@protei-libs/store"
import { CreateDeliverySpecification } from "@protei-portal/common"
import { SpecificationsCreateStore, SpecificationsCreateStore$type } from "../../store"
import { toJS } from "mobx"

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
    console.log("SSS", toJS(this.specificationsCreateStore.specification?.name))
    console.log("SSS", toJS(this.specificationsCreateStore.specification?.details))
    console.log("SSS", toJS(this.specificationsCreateStore.specification?.specifications))
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
  ) {
    this.specificationsCreateStore = specificationsCreateStore
  }

  private readonly specificationsCreateStore: SpecificationsCreateStore
  private readonly log = makeLogger("portal.delivery.spec.create")
}
