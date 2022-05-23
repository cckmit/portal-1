import { AnyToVoidFunction, NoneToVoidFunction } from "@protei-libs/types"

export type Scheduler = typeof scheduleNow | SchedulerCancellable
export type SchedulerCancellable =
  | typeof scheduleMicroTask
  | typeof scheduleRaf
  | typeof scheduleMacroTask

export function scheduleNow(fn: NoneToVoidFunction): void {
  fn()
}

export function scheduleMacroTask(fn: NoneToVoidFunction): AnyToVoidFunction {
  const id = setTimeout(fn, 0)
  return () => {
    clearTimeout(id)
  }
}

let microtasks: Array<NoneToVoidFunction> | undefined
export function scheduleMicroTaskPrimary(fn: NoneToVoidFunction): AnyToVoidFunction {
  return scheduleMicroTask(fn, true)
}
export function scheduleMicroTask(
  fn: NoneToVoidFunction,
  primary: boolean = false,
): AnyToVoidFunction {
  if (!microtasks) {
    microtasks = [fn]
    queueMicrotask(() => {
      const tasks = microtasks ?? []
      microtasks = undefined
      tasks.forEach(scheduleNow)
    })
  } else if (primary) {
    microtasks.unshift(fn)
  } else {
    microtasks.push(fn)
  }
  return () => {
    microtasks = microtasks?.filter((rfn) => rfn !== fn)
  }
}

let rafs: Array<NoneToVoidFunction> | undefined
export function scheduleRafPrimary(fn: NoneToVoidFunction): AnyToVoidFunction {
  return scheduleRaf(fn, true)
}
export function scheduleRaf(fn: NoneToVoidFunction, primary: boolean = false): AnyToVoidFunction {
  if (!rafs) {
    rafs = [fn]
    requestAnimationFrame(() => {
      const tasks = rafs ?? []
      rafs = undefined
      tasks.forEach(scheduleNow)
    })
  } else if (primary) {
    rafs.unshift(fn)
  } else {
    rafs.push(fn)
  }
  return () => {
    rafs = rafs?.filter((rfn) => rfn !== fn)
  }
}
