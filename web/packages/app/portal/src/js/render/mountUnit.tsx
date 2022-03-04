import { ReactNode } from "react"
import { Unit } from "../model/Unit"
import { mount } from "./mount"
import { unmount } from "./unmount"
import { Test1ComponentAsync } from "@protei-portal/unit-test1/src/jsmodule"
import { Test2ComponentAsync } from "@protei-portal/unit-test2/src/jsmodule"

export function mountUnit(
  container: Element | DocumentFragment,
  unit: Unit,
  emitter: EventTarget,
): void {
  const component = findComponentForUnit(unit)
  mount(container, component)
  emitter.addEventListener("detach", () => {
    unmount(container)
  })
}

function findComponentForUnit(unit: Unit): ReactNode {
  switch (unit) {
    case Unit.test1:
      return <Test1ComponentAsync />
    case Unit.test2:
      return <Test2ComponentAsync />
  }
}
