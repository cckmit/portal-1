import { Container, interfaces } from "inversify"

export const iocContainerCommon: interfaces.Container = new Container()

export function useIoCContainer(): interfaces.Container {
  return iocContainerCommon
}

export function useIoCBinding<T>(identifier: interfaces.ServiceIdentifier<T>): T {
  const container = useIoCContainer()
  return container.get<T>(identifier)
}
