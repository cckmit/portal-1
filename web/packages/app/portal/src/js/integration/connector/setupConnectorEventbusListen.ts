type Target<T> = {
  addListener(listener: (data: T) => unknown): void
  removeListener(listener: (data: T) => unknown): void
}

type Unsubscribe = () => void

export function setupConnectorEventbusListen<T>(
  target: Target<T>,
  onMessage: (message: T) => void,
  options?: {
    onError?: (e: Error | unknown) => void
  },
): Unsubscribe {
  const listener = (data: T) => {
    try {
      onMessage(data)
    } catch (e) {
      options?.onError?.(e)
    }
  }
  target.addListener(listener)
  return () => {
    target.removeListener(listener)
  }
}
