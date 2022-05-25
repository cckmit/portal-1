import { format as fnsFormat, formatDistanceToNow } from "date-fns"

export function dateFormat(date: Date, format: string, locale?: string): string
export function dateFormat(date: undefined, format: string, locale?: string): undefined
export function dateFormat(date: Date | undefined, format: string, locale?: string): string | undefined
export function dateFormat(date: Date | undefined, format: string, locale?: string): string | undefined {
  if (date === undefined) {
    return undefined
  }
  return fnsFormat(date, format, {
    // eslint-disable-next-line @typescript-eslint/no-unsafe-assignment
    locale: locale && require(`date-fns/locale/${adjustLocale(locale)}/index.js`) || undefined,
    weekStartsOn: 1,
  })
}

export function dateFormatDistanceToNow(date: Date, locale?: string): string
export function dateFormatDistanceToNow(date: undefined, locale?: string): undefined
export function dateFormatDistanceToNow(date: Date | undefined, locale?: string): string | undefined
export function dateFormatDistanceToNow(date: Date | undefined, locale?: string): string | undefined {
  if (date === undefined) {
    return undefined
  }
  return formatDistanceToNow(date, {
    // eslint-disable-next-line @typescript-eslint/no-unsafe-assignment
    locale: locale && require(`date-fns/locale/${adjustLocale(locale)}/index.js`) || undefined,
  })
}

export function dateFormatSeconds(seconds: number): string
export function dateFormatSeconds(seconds: undefined): undefined
export function dateFormatSeconds(seconds: number | undefined): string | undefined
export function dateFormatSeconds(seconds: number | undefined): string | undefined {
  if (seconds === undefined) {
    return undefined
  }
  const min = String(Math.floor(seconds / 60)).padStart(2, "0")
  const sec = String(seconds % 60).padStart(2, "0")
  return `${min}:${sec}`
}

function adjustLocale(locale: string): string {
  if (locale === "en") {
    return "en-US"
  }
  return locale
}
