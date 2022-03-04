import { ConnectorMessage } from "../connector/ConnectorMessage"

declare global {
  interface Window {
    ProteiPortalGwtBridge:
      | undefined
      | {
          messageOut?: (message: ConnectorMessage) => void
          messageIn?: (message: ConnectorMessage) => void
        }
  }
}
