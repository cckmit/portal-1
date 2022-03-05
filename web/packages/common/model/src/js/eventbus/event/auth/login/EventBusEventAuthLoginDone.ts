import { EventBusEvent, EventSource } from "@protei-libs/eventbus"
import { EventBusSourcePortalUi } from "../../../source/EventBusSource"
import { PersonId, UserLoginId } from "../../../../entity"

export const EventBusEventAuthLoginDoneType = "@app-portal/auth-login-done"

export type EventBusEventAuthLoginDone = ReturnType<typeof eventBusEventAuthLoginDone>

export type EventBusEventAuthLoginDonePayload = {
  personId: PersonId
  loginId: UserLoginId
}

export const eventBusEventAuthLoginDone = (
  payload: EventBusEventAuthLoginDonePayload,
  options?: { source?: EventSource },
): EventBusEvent<EventBusEventAuthLoginDonePayload> =>
  <const>{
    type: EventBusEventAuthLoginDoneType,
    source: options?.source ?? EventBusSourcePortalUi,
    payload: payload,
  }
