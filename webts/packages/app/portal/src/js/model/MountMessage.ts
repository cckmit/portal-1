import { Unit } from "./Unit"

export interface MountMessage {
  container: Element
  unit: Unit
  emitter: EventTarget
}
