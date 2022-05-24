import { EventBusEvent, EventSource } from "@protei-libs/eventbus"
import { EventBusSourcePortalUi } from "../../source/EventBusSource"
import { AppNotificationId } from "../../../entity"

export const EventAppNotificationRemoveType = "@app-portal/notification-remove"

export type EventAppNotificationRemove = ReturnType<typeof eventAppNotificationRemove>

export type EventAppNotificationRemovePayload = {
  notificationId: AppNotificationId
}

export const eventAppNotificationRemove = (payload: EventAppNotificationRemovePayload, options?: { source?: EventSource }): EventBusEvent<EventAppNotificationRemovePayload> => (<const>{
  type: EventAppNotificationRemoveType,
  source: options?.source ?? EventBusSourcePortalUi,
  payload: payload,
})
