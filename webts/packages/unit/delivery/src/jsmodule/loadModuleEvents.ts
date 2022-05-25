import { EventBusEvent } from "@protei-libs/eventbus"

export type LoadModuleEvent = {
  type: EventBusEvent<unknown>["type"]
  source?: EventBusEvent<unknown>["source"]
}

export const loadModuleEvents: Array<LoadModuleEvent> = []
