import { makeObservableStore } from "@protei-libs/store"
import { CreateDeliveryDetail, CreateDeliverySpecification, Progress, progressReady } from "@protei-portal/common"

export const SpecificationsImportStore$type = Symbol("SpecificationsImportStore")

export interface SpecificationsImportStore {
  specification: CreateDeliverySpecification | undefined
  details: Array<CreateDeliveryDetail>
  progress: Progress
  parse: {
    progress: Progress
    errors: Array<string>
  }
}

export const specificationsImportStore = makeObservableStore<SpecificationsImportStore>({
  specification: undefined,
  details: [],
  progress: progressReady(),
  parse: {
    progress: progressReady(),
    errors: [],
  },
})
