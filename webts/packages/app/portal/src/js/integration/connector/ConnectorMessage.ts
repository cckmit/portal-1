export interface ConnectorMessage<T = unknown> {
  type: ConnectorMessageTypes
  payload: T
}

export type ConnectorMessageTypes = "eventbus-event" | "mount"
