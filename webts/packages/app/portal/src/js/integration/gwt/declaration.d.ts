import { ConnectorMessage } from "../connector/ConnectorMessage"

declare global {
  interface Window {
    Protei_PORTAL_Bridge:
      | undefined
      | {
          messageOut?: (message: ConnectorMessage) => void
          messageIn?: (message: ConnectorMessage) => void
        }
  }
}
