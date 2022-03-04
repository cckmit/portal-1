import { __bind, __bindTo, identity } from "./function"
import { pipe } from "./pipe"

export interface Task<A> {
  (): Promise<A>
}

export const map: <A, B>(f: (a: A) => B) => (t: Task<A>) => Task<B> = (f) => (t) => () =>
  t().then(f)

export const flatMap: <A, B>(f: (a: A) => Task<B>) => (t: Task<A>) => Task<B> = (f) => (t) => () =>
  t().then((a) => f(a)())

export const chain: <A, B>(f: (a: A) => Task<B>) => (t: Task<A>) => Task<B> = (f) => (t) => () =>
  t().then((a) => f(a)())

export const chainFirst: <A, B>(f: (a: A) => Task<B>) => (t: Task<A>) => Task<A> = (f) =>
  chain((a) =>
    pipe(
      f(a),
      map(() => a),
    ),
  )

export const flatten: <A>(tt: Task<Task<A>>) => Task<A> = chain(identity)

export const of: <A>(a: A) => Task<A> = (a) => () => Promise.resolve(a)

// eslint-disable-next-line @typescript-eslint/ban-types
export const Do: Task<{}> = of({})

export const bindTo = <N extends string>(name: N): (<A>(t: Task<A>) => Task<{ [K in N]: A }>) =>
  map(__bindTo(name))

export const bind = <N extends string, A, B>(
  name: Exclude<N, keyof A>,
  f: (a: A) => Task<B>,
): ((t: Task<A>) => Task<{ [K in keyof A | N]: K extends keyof A ? A[K] : B }>) =>
  chain((a) =>
    pipe(
      f(a),
      map((b) => __bind(a, name, b)),
    ),
  )
