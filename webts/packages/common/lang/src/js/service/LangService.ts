import { inject, injectable } from "inversify"
import { runInTransaction } from "@protei-libs/store"
import { AppLocale, supportedAppLocales } from "../struct"
import { langStore, LangStore, LangStore$type } from "../store"

export const LangService$type = Symbol("LangService")

export interface LangService {
  safeLocale(locale: string | undefined): AppLocale | undefined

  setAppLocale(locale: AppLocale): void
}

@injectable()
export class LangServiceImpl implements LangService {
  safeLocale(locale: string | undefined): AppLocale | undefined {
    if (locale === undefined) {
      return undefined
    }
    if (supportedAppLocales.indexOf(locale as AppLocale) !== -1) {
      return locale as AppLocale
    }
    return undefined
  }

  setAppLocale(locale: AppLocale): void {
    runInTransaction(() => {
      this.langStore.locale = locale
    })
  }

  constructor(@inject(LangStore$type) langStore: LangStore) {
    this.langStore = langStore
  }

  private readonly langStore: LangStore
}

export const langService: LangService = new LangServiceImpl(langStore)
