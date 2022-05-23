import { scheduleRaf } from "@protei-libs/scheduler"

export function animate(tick: () => boolean): void {
  scheduleRaf(() => {
    if (tick()) {
      animate(tick)
    }
  })
}
