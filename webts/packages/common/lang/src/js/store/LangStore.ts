import { observable } from "mobx"
import { AppLocale } from "../struct"

export const LangStore$type = Symbol("LangStore")

export interface LangStore {
  locale: AppLocale
}

export const langStore = observable<LangStore>({
  locale: "ru",
})
