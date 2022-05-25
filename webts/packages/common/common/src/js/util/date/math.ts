import { addDays, addMinutes, addMonths, addQuarters, addWeeks, addYears } from "date-fns"

export function dateAddYears(date: Date, years: number): Date
export function dateAddYears(date: undefined, years: number): undefined
export function dateAddYears(date: Date | undefined, years: number): Date | undefined
export function dateAddYears(date: Date | undefined, years: number): Date | undefined {
  if (date === undefined) {
    return undefined
  }
  return addYears(date, years)
}

export function dateAddQuarters(date: Date, quarters: number): Date
export function dateAddQuarters(date: undefined, quarters: number): undefined
export function dateAddQuarters(date: Date | undefined, quarters: number): Date | undefined
export function dateAddQuarters(date: Date | undefined, quarters: number): Date | undefined {
  if (date === undefined) {
    return undefined
  }
  return addQuarters(date, quarters)
}

export function dateAddMonths(date: Date, months: number): Date
export function dateAddMonths(date: undefined, months: number): undefined
export function dateAddMonths(date: Date | undefined, months: number): Date | undefined
export function dateAddMonths(date: Date | undefined, months: number): Date | undefined {
  if (date === undefined) {
    return undefined
  }
  return addMonths(date, months)
}

export function dateAddWeeks(date: Date, weeks: number): Date
export function dateAddWeeks(date: undefined, weeks: number): undefined
export function dateAddWeeks(date: Date | undefined, weeks: number): Date | undefined
export function dateAddWeeks(date: Date | undefined, weeks: number): Date | undefined {
  if (date === undefined) {
    return undefined
  }
  return addWeeks(date, weeks)
}

export function dateAddDays(date: Date, days: number): Date
export function dateAddDays(date: undefined, days: number): undefined
export function dateAddDays(date: Date | undefined, days: number): Date | undefined
export function dateAddDays(date: Date | undefined, days: number): Date | undefined {
  if (date === undefined) {
    return undefined
  }
  return addDays(date, days)
}

export function dateAddMinutes(date: Date, minutes: number): Date
export function dateAddMinutes(date: undefined, minutes: number): undefined
export function dateAddMinutes(date: Date | undefined, minutes: number): Date | undefined
export function dateAddMinutes(date: Date | undefined, minutes: number): Date | undefined {
  if (date === undefined) {
    return undefined
  }
  return addMinutes(date, minutes)
}
