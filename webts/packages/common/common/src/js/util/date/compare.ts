import { isAfter, isBefore, isSameSecond } from "date-fns"

export type Inclusivity = "()" | "[]" | "(]" | "[)"

export function dateIsBefore(date: Date | undefined, relative: Date): boolean {
  if (date === undefined) {
    return false
  }
  return isBefore(date, relative)
}

export function dateIsEqualOrBefore(date: Date | undefined, relative: Date): boolean {
  if (date === undefined) {
    return false
  }
  return isSameSecond(date, relative) ? true : dateIsBefore(date, relative)
}

export function dateIsAfter(date: Date | undefined, relative: Date): boolean {
  if (date === undefined) {
    return false
  }
  return isAfter(date, relative)
}

export function dateIsEqualOrAfter(date: Date | undefined, relative: Date): boolean {
  if (date === undefined) {
    return false
  }
  return isSameSecond(date, relative) ? true : dateIsAfter(date, relative)
}

export function dateIsBetween(date: Date | undefined, dateFrom: Date, dateTo: Date, inclusivity: Inclusivity): boolean {
  if (date === undefined) {
    return false
  }
  const isBeforeEqual = inclusivity[0] === "["
  const isAfterEqual = inclusivity[1] === "]"
  const isBefore = isBeforeEqual ? dateIsEqualOrBefore : dateIsBefore
  const isAfter = isAfterEqual ? dateIsEqualOrAfter : dateIsAfter
  return isBefore(dateFrom, date) && isAfter(dateTo, date)
}
