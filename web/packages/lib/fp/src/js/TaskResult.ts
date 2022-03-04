import { Exception } from "@protei-libs/exception"
import * as T from "./Task"
import { Task } from "./Task"
import * as R from "./Result"
import { Result } from "./Result"
import { flow } from "./flow"
import { pipe } from "./pipe"
import { __bind, __bindTo, identity } from "./function"

export interface TaskResult<A> extends Task<Result<A>> {}

export const exception: <A = never>(e: Exception) => TaskResult<A> = flow(R.exception, T.of)

export const ok: <A = never>(a: A) => TaskResult<A> = flow(R.ok, T.of)

export const fromResult: <A>(ma: Result<A>) => TaskResult<A> = R.fold(exception, (a) => ok(a))

export const fold: <A, B>(
  onException: (e: Exception) => Task<B>,
  onOk: (a: A) => Task<B>,
) => (tr: TaskResult<A>) => Task<B> = flow(R.fold, T.chain)

export const map: <A, B>(f: (a: A) => B) => (tr: TaskResult<A>) => TaskResult<B> = (f) =>
  T.map(R.map(f))

export const mapException: (
  f: (e: Exception) => Exception,
) => <A>(tr: TaskResult<A>) => TaskResult<A> = (f) => T.map(R.mapException(f))

export const chain: <A, B>(f: (a: A) => TaskResult<B>) => (tr: TaskResult<A>) => TaskResult<B> =
  (f) => (tr) =>
    pipe(tr, T.chain(R.fold(exception, f)))

export const chainFirst: <A, B>(
  f: (a: A) => TaskResult<B>,
) => (tr: TaskResult<A>) => TaskResult<A> = (f) =>
  chain((a) =>
    pipe(
      f(a),
      map(() => a),
    ),
  )

export const flatten: <A>(ttr: TaskResult<TaskResult<A>>) => TaskResult<A> = chain(identity)

export const of: <A>(a: A) => TaskResult<A> = ok

// eslint-disable-next-line @typescript-eslint/ban-types
export const Do: TaskResult<{}> = of({})

export const bindTo = <N extends string>(
  name: N,
): (<A>(tr: TaskResult<A>) => TaskResult<{ [K in N]: A }>) => map(__bindTo(name))

export const bind: <N extends string, A, B>(
  name: Exclude<N, keyof A>,
  f: (a: A) => TaskResult<B>,
) => (tr: TaskResult<A>) => TaskResult<{ [K in keyof A | N]: K extends keyof A ? A[K] : B }> = (
  name,
  f,
) =>
  chain((a) =>
    pipe(
      f(a),
      map((b) => __bind(a, name, b)),
    ),
  )
