import { ReactNode } from "react"
import { Unit } from "./model/Unit"
import { DeliveryComponentAsync } from "@protei-portal/unit-delivery/src/jsmodule"

export function findComponentForUnit(unit: Unit): ReactNode | null {
  switch (unit) {
    case Unit.delivery:
      return <DeliveryComponentAsync />
  }
  return null
}
