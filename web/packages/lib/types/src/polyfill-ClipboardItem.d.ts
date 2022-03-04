import { AnyToVoidFunction } from "./js/function"

declare global {
  interface Window {
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    ClipboardItem?: any
    requestIdleCallback: (cb: AnyToVoidFunction) => void
  }

  interface Clipboard {
    write?: AnyToVoidFunction
  }
}
