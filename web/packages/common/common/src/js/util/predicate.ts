export type Diff<T, U> = T extends U ? never : T
export type Predicate<I, O extends I> = (i: I) => i is O

export const and =
  <I, O1 extends I, O2 extends I>(p1: Predicate<I, O1>, p2: Predicate<I, O2>) =>
  (i: I): i is O1 & O2 =>
    p1(i) && p2(i)

export const or =
  <I, O1 extends I, O2 extends I>(p1: Predicate<I, O1>, p2: Predicate<I, O2>) =>
  (i: I): i is O1 | O2 =>
    p1(i) || p2(i)

export const not =
  <I, O extends I>(p: Predicate<I, O>) =>
  (i: I): i is Diff<I, O> =>
    !p(i)

export const isNull = <I>(i: I | null): i is null => i === null

export const isUndefined = <I>(i: I | undefined): i is undefined => i === undefined

export const isNotUndefined = <I>(i: I | undefined): i is I => i !== undefined

export const isNotNull = <I>(i: I | null | undefined): i is I => i !== null && i !== undefined

export const isNotNaN = <I extends number>(i: I | null | undefined): i is I => i !== null && i !== undefined && !isNaN(i)
