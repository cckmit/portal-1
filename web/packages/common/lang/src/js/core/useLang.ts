import { Lang } from "../Lang"
import { getLang } from "./getLang"

export function useLang(): Lang {
  return getLang()
}
