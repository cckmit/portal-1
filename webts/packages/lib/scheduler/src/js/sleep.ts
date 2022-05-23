export function sleep(ms: number): Promise<void> {
  return new Promise((resolve) => setTimeout(() => resolve(), ms))
}

export function sleepResolve<T = undefined>(ms: number, value: T): Promise<T> {
  return new Promise((resolve) => setTimeout(() => resolve(value), ms))
}

export function sleepReject<T = undefined>(ms: number, error: Error | unknown): Promise<T> {
  return new Promise((_, reject) => setTimeout(() => reject(error), ms))
}
