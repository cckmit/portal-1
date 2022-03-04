import { Exception } from "@protei-libs/exception"
import { __bind, __bindTo, identity } from "./function"
import { pipe } from "./pipe"

export type Result<A> = ResultException | ResultOk<A>

export interface ResultException {
  readonly _tag: "ResultException"
  readonly error: Exception
}

export interface ResultOk<A> {
  readonly _tag: "ResultOk"
  readonly data: A
}

export const exception = <A = never>(e: Exception): Result<A> => ({
  _tag: "ResultException",
  error: e,
})

export const ok = <A = never>(a: A): Result<A> => ({ _tag: "ResultOk", data: a })

export const isException = <A>(r: Result<A>): r is ResultException => r._tag === "ResultException"

export const isOk = <A>(r: Result<A>): r is ResultOk<A> => r._tag === "ResultOk"

export const fold: <A, B>(
  onException: (e: Exception) => B,
  onOk: (a: A) => B,
) => (r: Result<A>) => B = (onException, onOk) => (r) =>
  isException(r) ? onException(r.error) : onOk(r.data)

export const map: <A, B>(map: (a: A) => B) => (r: Result<A>) => Result<B> = (map) => (r) =>
  isException(r) ? r : ok(map(r.data))

export const flatMap: <A, B>(map: (a: A) => Result<B>) => (r: Result<A>) => Result<B> =
  (map) => (r) =>
    isException(r) ? r : map(r.data)

export const mapException: <A>(map: (e: Exception) => Exception) => (r: Result<A>) => Result<A> =
  (map) => (r) =>
    isException(r) ? exception(map(r.error)) : r

export const flatMapException: <A>(
  map: (e: Exception) => Result<A>,
) => (r: Result<A>) => Result<A> = (map) => (r) => isException(r) ? map(r.error) : r

export const chain: <A, B>(f: (a: A) => Result<B>) => (r: Result<A>) => Result<B> = (f) => (r) =>
  isException(r) ? r : f(r.data)

export const chainFirst: <A, B>(f: (a: A) => Result<B>) => (r: Result<A>) => Result<A> =
  (f) => (r) =>
    pipe(
      r,
      chain((a) =>
        pipe(
          f(a),
          map(() => a),
        ),
      ),
    )

export const flatten: <A>(rr: Result<Result<A>>) => Result<A> = chain(identity)

export const of: <A>(a: A) => Result<A> = ok

// eslint-disable-next-line @typescript-eslint/ban-types
export const Do: Result<{}> = of({})

export const bindTo = <N extends string>(name: N): (<A>(r: Result<A>) => Result<{ [K in N]: A }>) =>
  map(__bindTo(name))

export const bind: <N extends string, A, B>(
  name: Exclude<N, keyof A>,
  f: (a: A) => Result<B>,
) => (r: Result<A>) => Result<{ [K in keyof A | N]: K extends keyof A ? A[K] : B }> = (name, f) =>
  chain((a) =>
    pipe(
      f(a),
      map((b) => __bind(a, name, b)),
    ),
  )
