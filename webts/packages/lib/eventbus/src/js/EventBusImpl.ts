import { EventBus, EventListener, EventUnsubscribe, EventTopic } from "./EventBus"

export class EventBusImpl<T> implements EventBus<T> {
  private topicListeners: Map<EventTopic, Array<EventListener<T>>>
  private broadcastListeners: Array<EventListener<T>>

  constructor() {
    this.topicListeners = new Map<EventTopic, Array<EventListener<T>>>()
    this.broadcastListeners = []
  }

  subscribe(topic: EventTopic | Array<EventTopic>, listener: EventListener<T>): EventUnsubscribe {
    const topics = Array.isArray(topic) ? topic : [topic]
    const unsubscribe = topics.map((topic) => {
      if (!this.topicListeners.has(topic)) {
        this.topicListeners.set(topic, [])
      }
      this.topicListeners.get(topic)?.push(listener)
      return () => {
        this.unsubscribe(topic, listener)
      }
    })
    return () => {
      unsubscribe.forEach((un) => un())
    }
  }

  subscribeBroadcast(listener: EventListener<T>): EventUnsubscribe {
    this.broadcastListeners.push(listener)
    return () => {
      this.unsubscribeBroadcast(listener)
    }
  }

  unsubscribe(topic: EventTopic | Array<EventTopic>, listener: EventListener<T>): void {
    const topics = Array.isArray(topic) ? topic : [topic]
    topics.forEach((topic) => {
      const t2l = this.topicListeners.get(topic)?.filter((lt) => lt !== listener) ?? []
      this.topicListeners.set(topic, t2l)
    })
  }

  unsubscribeBroadcast(listener: EventListener<T>): void {
    this.broadcastListeners = this.broadcastListeners.filter((item) => item !== listener)
  }

  send(topic: EventTopic, payload: T): void {
    const topicListeners = this.topicListeners.get(topic) ?? []
    const broadcastListeners = this.broadcastListeners
    const listeners = topicListeners.concat(broadcastListeners)
    for (const listener of listeners) {
      void listener(payload)
    }
  }
}
