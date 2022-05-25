import { Unit } from "../model/Unit"
import { mount } from "./mount"
import { unmount } from "./unmount"
import { findComponentForUnit } from "../findComponentForUnit"

export function mountUnit(container: Element | DocumentFragment, unit: Unit, emitter: EventTarget): void {
  const component = findComponentForUnit(unit)
  if (component === null) {
    return
  }
  mount(container, component)
  emitter.addEventListener("detach", () => {
    unmount(container)
  })
}
