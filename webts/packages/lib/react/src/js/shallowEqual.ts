import { PropsWithChildren } from "react"

export function shallowEqual(
  prev: Readonly<PropsWithChildren<Record<string | number | symbol, unknown>>>,
  next: Readonly<PropsWithChildren<Record<string | number | symbol, unknown>>>,
): boolean {
  if (shallowEqualAny(prev, next)) {
    return true
  }

  if (typeof prev !== "object" || prev === null || typeof next !== "object" || next === null) {
    return false
  }

  const keysA = Object.keys(prev)
  const keysB = Object.keys(next)

  if (keysA.length !== keysB.length) {
    return false
  }

  for (let i = 0; i < keysA.length; i++) {
    if (
      !Object.hasOwnProperty.call(next, keysA[i]) ||
      !shallowEqualAny(prev[keysA[i]], next[keysA[i]])
    ) {
      return false
    }
  }

  return true
}

export function shallowEqualAny(prev: unknown, next: unknown): boolean {
  if (Object.is(prev, next)) {
    return true
  }

  if (Array.isArray(prev) && Array.isArray(next)) {
    return shallowEqualArrays(prev, next)
  }

  return false
}

export function shallowEqualArrays(arrA: Array<unknown>, arrB: Array<unknown>): boolean {
  if (arrA === arrB) {
    return true
  }

  if (!arrA || !arrB) {
    return false
  }

  const len = arrA.length

  if (arrB.length !== len) {
    return false
  }

  for (let i = 0; i < len; i++) {
    if (arrA[i] !== arrB[i]) {
      return false
    }
  }

  return true
}
