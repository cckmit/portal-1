import { AppLocale } from "../struct"
import { langStore } from "../store"

export function useLocale(): AppLocale {
  const locale = langStore.locale
  return locale
}
