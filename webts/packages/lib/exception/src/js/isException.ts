/* eslint-disable @typescript-eslint/no-explicit-any */
import { Exception } from "./Exception"

export const isException = (entity: any | undefined | null): entity is Exception =>
  entity != undefined &&
  typeof entity === "object" &&
  "_tag" in entity &&
  // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
  entity._tag === "Exception"

export const isExceptionNamed = (entity: any | undefined | null, name: string): boolean =>
  isException(entity) && nameMatch(nameSplit(entity.name), nameSplit(name))

function nameSplit(name: string): Array<string> {
  return name.split("_")
}

function nameMatch(target: Array<string>, match: Array<string>): boolean {
  if (target === match) {
    return true
  }
  if (match.length > target.length) {
    return false
  }
  for (let index = 0; index < match.length; index++) {
    if (target[index] !== match[index]) {
      return false
    }
  }
  return true
}
