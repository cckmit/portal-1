/* eslint-disable @typescript-eslint/no-explicit-any */

export type AnyFunction = (...args: any) => any
export type AnyToVoidFunction = (...args: any) => void
export type ParametersToVoidFunction<T extends AnyFunction> = (...args: Parameters<T>) => void
export type NoneToVoidFunction = () => void
