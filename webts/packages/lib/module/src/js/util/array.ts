export function arrayClear<T>(array: Array<T>): void {
  array.splice(0, array.length)
}

export function arrayFilterInPlace<T>(
  array: Array<T>,
  predicate: (value: T, index: number, array: Array<T>) => boolean,
): Array<T> {
  let i = 0,
    j = 0
  while (i < array.length) {
    const val = array[i]
    if (predicate(val, i, array)) {
      array[j++] = val
    }
    i++
  }
  array.length = j
  return array
}
