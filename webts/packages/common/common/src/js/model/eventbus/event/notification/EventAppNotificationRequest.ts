import { EventBusEvent, EventSource } from "@protei-libs/eventbus"
import { EventBusSourcePortalUi } from "../../source/EventBusSource"
import { AppNotification } from "../../../entity"

export const EventAppNotificationRequestType = "@app-portal/notification-request"

export type EventAppNotificationRequest = ReturnType<typeof eventAppNotificationRequest>

export type EventAppNotificationRequestPayload = {
  notification: AppNotification
}

export const eventAppNotificationRequest = (payload: EventAppNotificationRequestPayload, options?: { source?: EventSource }): EventBusEvent<EventAppNotificationRequestPayload> => (<const>{
  type: EventAppNotificationRequestType,
  source: options?.source ?? EventBusSourcePortalUi,
  payload: payload,
})
