import { Container, interfaces } from "inversify"

export const iocContainer: interfaces.Container = new Container()

export function useIoCContainer(): interfaces.Container {
  return iocContainer
}

export function useIoCBinding<T>(identifier: interfaces.ServiceIdentifier<T>): T {
  const container = useIoCContainer()
  return container.get<T>(identifier)
}
