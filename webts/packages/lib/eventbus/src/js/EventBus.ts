export type EventTopic = string
export type EventSource = string
export type EventUnsubscribe = () => void
export type EventListener<T> = (payload: T) => void | Promise<void>

export interface EventBus<T> {
  subscribe(topic: EventTopic | Array<EventTopic>, listener: EventListener<T>): EventUnsubscribe
  unsubscribe(topic: EventTopic | Array<EventTopic>, listener: EventListener<T>): void
  subscribeBroadcast(listener: EventListener<T>): EventUnsubscribe
  unsubscribeBroadcast(listener: EventListener<T>): void
  send(topic: EventTopic, payload: T): void
}
