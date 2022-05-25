import { ConnectorMessage } from "../connector/ConnectorMessage"
import { MountMessage } from "../../model/MountMessage"
import { Unit } from "../../model/Unit"
import { mountUnit } from "../../render/mountUnit"

export function onMount(message: ConnectorMessage): void {
  if (message.type !== "mount") {
    return
  }
  const msg = message as ConnectorMessage<MountMessage>
  const payload = msg.payload
  if (!("container" in payload)) {
    return
  }
  const container = payload.container
  if (!("unit" in payload)) {
    return
  }
  const unit = payload.unit
  if (!Object.values(Unit).includes(unit)) {
    return
  }
  if (!("emitter" in payload)) {
    return
  }
  const emitter = payload.emitter
  mountUnit(container, unit, emitter)
}
