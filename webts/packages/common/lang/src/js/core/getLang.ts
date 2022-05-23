import { Lang } from "../Lang"
import { langStore } from "../store"
import { langRu } from "../LangRu"
import { langEn } from "../LangEn"

export function getLang(): Lang {
  const locale = langStore.locale
  switch (locale) {
    case "ru":
      return langRu
    case "en":
      return langEn
  }
}
