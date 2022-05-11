import { formatISO, getUnixTime } from "date-fns"

export function dateToUnix(date: Date): number
export function dateToUnix(date: undefined): undefined
export function dateToUnix(date: Date | undefined): number | undefined
export function dateToUnix(date: Date | undefined): number | undefined {
  if (date === undefined) {
    return undefined
  }
  return getUnixTime(date)
}

export function dateToIso(date: Date): string
export function dateToIso(date: undefined): undefined
export function dateToIso(date: Date | undefined): string | undefined
export function dateToIso(date: Date | undefined): string | undefined {
  if (date === undefined) {
    return undefined
  }
  return formatISO(date)
}

export function isoToDate(iso: string): Date
export function isoToDate(iso: undefined): undefined
export function isoToDate(iso: string | undefined): Date | undefined
export function isoToDate(iso: string | undefined): Date | undefined {
  if (iso === undefined) {
    return undefined
  }
  if (iso === "") {
    return undefined
  }
  return new Date(iso)
}

export function tsOrIsoToDate(tsOrIso: string | number): Date
export function tsOrIsoToDate(tsOrIso: undefined): undefined
export function tsOrIsoToDate(tsOrIso: string | number | undefined): Date | undefined
export function tsOrIsoToDate(tsOrIso: string | number | undefined): Date | undefined {
  if (tsOrIso === undefined) {
    return undefined
  }
  if (tsOrIso === "") {
    return undefined
  }
  return new Date(tsOrIso)
}
