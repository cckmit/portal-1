import { EventBusEvent, EventSource } from "@protei-libs/eventbus"
import { EventBusSourcePortalUi } from "../../../source/EventBusSource"

export const EventBusEventAuthLogoutDoneType = "@app-portal/auth-logout-done"

export type EventBusEventAuthLogoutDone = ReturnType<typeof eventBusEventAuthLogoutDone>

export type EventBusEventAuthLogoutDonePayload = {
  userInitiated: boolean
}

export const eventBusEventAuthLogoutDone = (
  payload: EventBusEventAuthLogoutDonePayload,
  options?: { source?: EventSource },
): EventBusEvent<EventBusEventAuthLogoutDonePayload> =>
  <const>{
    type: EventBusEventAuthLogoutDoneType,
    source: options?.source ?? EventBusSourcePortalUi,
    payload: payload,
  }
