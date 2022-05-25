import { EventBusEvent, EventSource } from "@protei-libs/eventbus"
import { EventBusSourcePortalUi } from "../source/EventBusSource"

export const EventBusEventTestType = "@app-portal/test-event"

export type EventBusEventTest = ReturnType<typeof eventBusEventTest>

export type EventBusEventTestPayload = {
  text: string
}

export const eventBusEventTest = (
  payload: EventBusEventTestPayload,
  options?: { source?: EventSource },
): EventBusEvent<EventBusEventTestPayload> =>
  <const>{
    type: EventBusEventTestType,
    source: options?.source ?? EventBusSourcePortalUi,
    payload: payload,
  }
