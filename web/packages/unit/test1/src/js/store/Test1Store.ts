import { observable } from "mobx"

export const Test1Store$type = Symbol("Test1Store")

export interface Test1Store {
  counter: number
  interval: number | undefined
}

export const test1Store = observable<Test1Store>({
  counter: 0,
  interval: undefined,
})
