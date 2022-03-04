export interface ConnectorMessage<T = unknown> {
  type: ConnectorMessageTypes
  payload: T
}

export type ConnectorMessageTypes = "portal-eventbus-event" | "portal-mount"
