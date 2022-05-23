import {
  endOfDay,
  endOfMonth,
  endOfQuarter,
  endOfWeek,
  endOfYear,
  startOfDay,
  startOfMonth,
  startOfQuarter,
  startOfWeek,
  startOfYear,
} from "date-fns"

export function dateStartOfDay(date: Date): Date
export function dateStartOfDay(date: undefined): undefined
export function dateStartOfDay(date: Date | undefined): Date | undefined
export function dateStartOfDay(date: Date | undefined): Date | undefined {
  if (date === undefined) {
    return undefined
  }
  return startOfDay(date)
}

export function dateEndOfDay(date: Date): Date
export function dateEndOfDay(date: undefined): undefined
export function dateEndOfDay(date: Date | undefined): Date | undefined
export function dateEndOfDay(date: Date | undefined): Date | undefined {
  if (date === undefined) {
    return undefined
  }
  return endOfDay(date)
}

export function dateStartOfWeek(date: Date): Date
export function dateStartOfWeek(date: undefined): undefined
export function dateStartOfWeek(date: Date | undefined): Date | undefined
export function dateStartOfWeek(date: Date | undefined): Date | undefined {
  if (date === undefined) {
    return undefined
  }
  return startOfWeek(date, { weekStartsOn: 1 })
}

export function dateEndOfWeek(date: Date): Date
export function dateEndOfWeek(date: undefined): undefined
export function dateEndOfWeek(date: Date | undefined): Date | undefined
export function dateEndOfWeek(date: Date | undefined): Date | undefined {
  if (date === undefined) {
    return undefined
  }
  return endOfWeek(date)
}

export function dateStartOfMonth(date: Date): Date
export function dateStartOfMonth(date: undefined): undefined
export function dateStartOfMonth(date: Date | undefined): Date | undefined
export function dateStartOfMonth(date: Date | undefined): Date | undefined {
  if (date === undefined) {
    return undefined
  }
  return startOfMonth(date)
}

export function dateEndOfMonth(date: Date): Date
export function dateEndOfMonth(date: undefined): undefined
export function dateEndOfMonth(date: Date | undefined): Date | undefined
export function dateEndOfMonth(date: Date | undefined): Date | undefined {
  if (date === undefined) {
    return undefined
  }
  return endOfMonth(date)
}

export function dateStartOfQuarter(date: Date): Date
export function dateStartOfQuarter(date: undefined): undefined
export function dateStartOfQuarter(date: Date | undefined): Date | undefined
export function dateStartOfQuarter(date: Date | undefined): Date | undefined {
  if (date === undefined) {
    return undefined
  }
  return startOfQuarter(date)
}

export function dateEndOfQuarter(date: Date): Date
export function dateEndOfQuarter(date: undefined): undefined
export function dateEndOfQuarter(date: Date | undefined): Date | undefined
export function dateEndOfQuarter(date: Date | undefined): Date | undefined {
  if (date === undefined) {
    return undefined
  }
  return endOfQuarter(date)
}

export function dateStartOfYear(date: Date): Date
export function dateStartOfYear(date: undefined): undefined
export function dateStartOfYear(date: Date | undefined): Date | undefined
export function dateStartOfYear(date: Date | undefined): Date | undefined {
  if (date === undefined) {
    return undefined
  }
  return startOfYear(date)
}

export function dateEndOfYear(date: Date): Date
export function dateEndOfYear(date: undefined): undefined
export function dateEndOfYear(date: Date | undefined): Date | undefined
export function dateEndOfYear(date: Date | undefined): Date | undefined {
  if (date === undefined) {
    return undefined
  }
  return endOfYear(date)
}
