import { EventBusEvent } from "@protei-libs/eventbus"

export type AppNotificationId = string

export interface AppNotification {
  notificationId: AppNotificationId
  type: "default" | "warn" | "error" | "success"
  message: string
  iconFont?: string
  iconUrl?: string
  imageUrl?: string
  timestamp?: number
  duration?: number
  silent?: boolean
  actions?: Array<{
    title: string
    iconUrl?: string
    event: EventBusEvent<unknown>
  }>
}
