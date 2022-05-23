/* eslint-disable @typescript-eslint/ban-types */

export type Unsubscribe = () => void
export type PromiseType<T> = T extends PromiseLike<infer U> ? U : T
export type TypeOfClassMethod<T, M extends keyof T> = T[M] extends Function ? T[M] : never
