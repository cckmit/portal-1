import { iocContainerCommon } from "./ioc"
import { iocCommon } from "./ioc-module"
import { AuthService$type } from "./service"

export function setup(): void {
  iocContainerCommon.load(iocCommon)
  iocContainerCommon.get(AuthService$type) // TODO eager
}
