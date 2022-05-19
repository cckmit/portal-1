import { makeObservableStore } from "@protei-libs/store"
import { Progress, progressReady } from "@protei-portal/common"

export const SpecificationsImportStore$type = Symbol("SpecificationsImportStore")

export interface SpecificationsImportStore {
  progress: Progress
  errors: Array<string>
}

export const specificationsImportStore = makeObservableStore<SpecificationsImportStore>({
  progress: progressReady(),
  errors: [],
})
