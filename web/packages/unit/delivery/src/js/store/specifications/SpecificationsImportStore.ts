import { makeObservableStore } from "@protei-libs/store"

export const SpecificationsImportStore$type = Symbol("SpecificationsImportStore")

export interface SpecificationsImportStore {
  name: string
}

export const specificationsImportStore = makeObservableStore<SpecificationsImportStore>({
  name: "",
})
