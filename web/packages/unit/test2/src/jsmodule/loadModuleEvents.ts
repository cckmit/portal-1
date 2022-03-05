import { EventBusEvent } from "@protei-libs/eventbus"
import { EventBusEventTestType } from "@protei-portal/common-model"

export type LoadModuleEvent = {
  type: EventBusEvent<unknown>["type"]
  source?: EventBusEvent<unknown>["source"]
}

export const loadModuleEvents: Array<LoadModuleEvent> = [
  // { type: EventBusEventTestType }, // TODO remove
]
