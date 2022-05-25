import { makeObservableStore } from "@protei-libs/store"
import { CreateDeliverySpecification, Progress, progressReady } from "@protei-portal/common"

export const SpecificationsCreateStore$type = Symbol("SpecificationsCreateStore")

export interface SpecificationsCreateStore {
  specification: CreateDeliverySpecification | undefined
  progress: Progress
}

export const specificationsCreateStore = makeObservableStore<SpecificationsCreateStore>({
  specification: undefined,
  progress: progressReady(),
})
