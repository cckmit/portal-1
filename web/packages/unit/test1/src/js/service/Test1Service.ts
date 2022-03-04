import { runInAction } from "mobx"
import { inject, injectable } from "inversify"
import { Test1Store, Test1Store$type } from "../store/Test1Store"

export const Test1Service$type = Symbol("Test1Service")

export interface Test1Service {
  start(): void

  stop(): void
}

@injectable()
export class Test1ServiceImpl implements Test1Service {
  start(): void {
    this.stop()
    runInAction(() => {
      this.test1Store.interval = window.setTimeout(() => {
        runInAction(() => {
          this.test1Store.counter++
        })
      }, 1000)
    })
  }

  stop(): void {
    if (this.test1Store.interval !== undefined) {
      window.clearInterval(this.test1Store.interval)
      runInAction(() => {
        this.test1Store.interval = undefined
      })
    }
  }

  constructor(@inject(Test1Store$type) test1Store: Test1Store) {
    this.test1Store = test1Store
  }

  private readonly test1Store: Test1Store
}
