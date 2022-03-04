/* eslint-disable @typescript-eslint/no-non-null-assertion,@typescript-eslint/ban-types */

export function pipeAsync<A>(a: A): A

export function pipeAsync<A, B>(a: A, ab: (a: A) => B | PromiseLike<B>): Promise<B>

export function pipeAsync<A, B, C>(
  a: A,
  ab: (a: A) => B | PromiseLike<B>,
  bc: (b: B) => C | PromiseLike<C>,
): Promise<C>

export function pipeAsync<A, B, C, D>(
  a: A,
  ab: (a: A) => B | PromiseLike<B>,
  bc: (b: B) => C | PromiseLike<C>,
  cd: (c: C) => D | PromiseLike<D>,
): Promise<D>

export function pipeAsync<A, B, C, D, E>(
  a: A,
  ab: (a: A) => B | PromiseLike<B>,
  bc: (b: B) => C | PromiseLike<C>,
  cd: (c: C) => D | PromiseLike<D>,
  de: (d: D) => E | PromiseLike<E>,
): Promise<E>

export function pipeAsync<A, B, C, D, E, F>(
  a: A,
  ab: (a: A) => B | PromiseLike<B>,
  bc: (b: B) => C | PromiseLike<C>,
  cd: (c: C) => D | PromiseLike<D>,
  de: (d: D) => E | PromiseLike<E>,
  ef: (e: E) => F | PromiseLike<F>,
): Promise<F>

export function pipeAsync<A, B, C, D, E, F, G>(
  a: A,
  ab: (a: A) => B | PromiseLike<B>,
  bc: (b: B) => C | PromiseLike<C>,
  cd: (c: C) => D | PromiseLike<D>,
  de: (d: D) => E | PromiseLike<E>,
  ef: (e: E) => F | PromiseLike<F>,
  fg: (f: F) => G | PromiseLike<G>,
): Promise<G>

export function pipeAsync<A, B, C, D, E, F, G, H>(
  a: A,
  ab: (a: A) => B | PromiseLike<B>,
  bc: (b: B) => C | PromiseLike<C>,
  cd: (c: C) => D | PromiseLike<D>,
  de: (d: D) => E | PromiseLike<E>,
  ef: (e: E) => F | PromiseLike<F>,
  fg: (f: F) => G | PromiseLike<G>,
  gh: (g: G) => H | PromiseLike<H>,
): Promise<H>

export function pipeAsync<A, B, C, D, E, F, G, H, I>(
  a: A,
  ab: (a: A) => B | PromiseLike<B>,
  bc: (b: B) => C | PromiseLike<C>,
  cd: (c: C) => D | PromiseLike<D>,
  de: (d: D) => E | PromiseLike<E>,
  ef: (e: E) => F | PromiseLike<F>,
  fg: (f: F) => G | PromiseLike<G>,
  gh: (g: G) => H | PromiseLike<H>,
  hi: (h: H) => I | PromiseLike<I>,
): Promise<I>

export function pipeAsync<A, B, C, D, E, F, G, H, I, J>(
  a: A,
  ab: (a: A) => B | PromiseLike<B>,
  bc: (b: B) => C | PromiseLike<C>,
  cd: (c: C) => D | PromiseLike<D>,
  de: (d: D) => E | PromiseLike<E>,
  ef: (e: E) => F | PromiseLike<F>,
  fg: (f: F) => G | PromiseLike<G>,
  gh: (g: G) => H | PromiseLike<H>,
  hi: (h: H) => I | PromiseLike<I>,
  ij: (i: I) => J | PromiseLike<J>,
): Promise<J>

export function pipeAsync<A, B, C, D, E, F, G, H, I, J, K>(
  a: A,
  ab: (a: A) => B | PromiseLike<B>,
  bc: (b: B) => C | PromiseLike<C>,
  cd: (c: C) => D | PromiseLike<D>,
  de: (d: D) => E | PromiseLike<E>,
  ef: (e: E) => F | PromiseLike<F>,
  fg: (f: F) => G | PromiseLike<G>,
  gh: (g: G) => H | PromiseLike<H>,
  hi: (h: H) => I | PromiseLike<I>,
  ij: (i: I) => J | PromiseLike<J>,
  jk: (j: J) => K | PromiseLike<K>,
): Promise<K>

export function pipeAsync<A, B, C, D, E, F, G, H, I, J, K, L>(
  a: A,
  ab: (a: A) => B | PromiseLike<B>,
  bc: (b: B) => C | PromiseLike<C>,
  cd: (c: C) => D | PromiseLike<D>,
  de: (d: D) => E | PromiseLike<E>,
  ef: (e: E) => F | PromiseLike<F>,
  fg: (f: F) => G | PromiseLike<G>,
  gh: (g: G) => H | PromiseLike<H>,
  hi: (h: H) => I | PromiseLike<I>,
  ij: (i: I) => J | PromiseLike<J>,
  jk: (j: J) => K | PromiseLike<K>,
  kl: (k: K) => L | PromiseLike<L>,
): Promise<L>

export function pipeAsync<A, B, C, D, E, F, G, H, I, J, K, L, M>(
  a: A,
  ab: (a: A) => B | PromiseLike<B>,
  bc: (b: B) => C | PromiseLike<C>,
  cd: (c: C) => D | PromiseLike<D>,
  de: (d: D) => E | PromiseLike<E>,
  ef: (e: E) => F | PromiseLike<F>,
  fg: (f: F) => G | PromiseLike<G>,
  gh: (g: G) => H | PromiseLike<H>,
  hi: (h: H) => I | PromiseLike<I>,
  ij: (i: I) => J | PromiseLike<J>,
  jk: (j: J) => K | PromiseLike<K>,
  kl: (k: K) => L | PromiseLike<L>,
  lm: (l: L) => M | PromiseLike<M>,
): Promise<M>

export function pipeAsync<A, B, C, D, E, F, G, H, I, J, K, L, M, N>(
  a: A,
  ab: (a: A) => B | PromiseLike<B>,
  bc: (b: B) => C | PromiseLike<C>,
  cd: (c: C) => D | PromiseLike<D>,
  de: (d: D) => E | PromiseLike<E>,
  ef: (e: E) => F | PromiseLike<F>,
  fg: (f: F) => G | PromiseLike<G>,
  gh: (g: G) => H | PromiseLike<H>,
  hi: (h: H) => I | PromiseLike<I>,
  ij: (i: I) => J | PromiseLike<J>,
  jk: (j: J) => K | PromiseLike<K>,
  kl: (k: K) => L | PromiseLike<L>,
  lm: (l: L) => M | PromiseLike<M>,
  mn: (m: M) => N | PromiseLike<N>,
): Promise<N>

export function pipeAsync<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O>(
  a: A,
  ab: (a: A) => B | PromiseLike<B>,
  bc: (b: B) => C | PromiseLike<C>,
  cd: (c: C) => D | PromiseLike<D>,
  de: (d: D) => E | PromiseLike<E>,
  ef: (e: E) => F | PromiseLike<F>,
  fg: (f: F) => G | PromiseLike<G>,
  gh: (g: G) => H | PromiseLike<H>,
  hi: (h: H) => I | PromiseLike<I>,
  ij: (i: I) => J | PromiseLike<J>,
  jk: (j: J) => K | PromiseLike<K>,
  kl: (k: K) => L | PromiseLike<L>,
  lm: (l: L) => M | PromiseLike<M>,
  mn: (m: M) => N | PromiseLike<N>,
  no: (n: N) => O | PromiseLike<O>,
): Promise<O>

export function pipeAsync<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P>(
  a: A,
  ab: (a: A) => B | PromiseLike<B>,
  bc: (b: B) => C | PromiseLike<C>,
  cd: (c: C) => D | PromiseLike<D>,
  de: (d: D) => E | PromiseLike<E>,
  ef: (e: E) => F | PromiseLike<F>,
  fg: (f: F) => G | PromiseLike<G>,
  gh: (g: G) => H | PromiseLike<H>,
  hi: (h: H) => I | PromiseLike<I>,
  ij: (i: I) => J | PromiseLike<J>,
  jk: (j: J) => K | PromiseLike<K>,
  kl: (k: K) => L | PromiseLike<L>,
  lm: (l: L) => M | PromiseLike<M>,
  mn: (m: M) => N | PromiseLike<N>,
  no: (n: N) => O | PromiseLike<O>,
  op: (o: O) => P | PromiseLike<P>,
): Promise<P>

export function pipeAsync<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q>(
  a: A,
  ab: (a: A) => B | PromiseLike<B>,
  bc: (b: B) => C | PromiseLike<C>,
  cd: (c: C) => D | PromiseLike<D>,
  de: (d: D) => E | PromiseLike<E>,
  ef: (e: E) => F | PromiseLike<F>,
  fg: (f: F) => G | PromiseLike<G>,
  gh: (g: G) => H | PromiseLike<H>,
  hi: (h: H) => I | PromiseLike<I>,
  ij: (i: I) => J | PromiseLike<J>,
  jk: (j: J) => K | PromiseLike<K>,
  kl: (k: K) => L | PromiseLike<L>,
  lm: (l: L) => M | PromiseLike<M>,
  mn: (m: M) => N | PromiseLike<N>,
  no: (n: N) => O | PromiseLike<O>,
  op: (o: O) => P | PromiseLike<P>,
  pq: (p: P) => Q | PromiseLike<Q>,
): Promise<Q>

export function pipeAsync<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R>(
  a: A,
  ab: (a: A) => B | PromiseLike<B>,
  bc: (b: B) => C | PromiseLike<C>,
  cd: (c: C) => D | PromiseLike<D>,
  de: (d: D) => E | PromiseLike<E>,
  ef: (e: E) => F | PromiseLike<F>,
  fg: (f: F) => G | PromiseLike<G>,
  gh: (g: G) => H | PromiseLike<H>,
  hi: (h: H) => I | PromiseLike<I>,
  ij: (i: I) => J | PromiseLike<J>,
  jk: (j: J) => K | PromiseLike<K>,
  kl: (k: K) => L | PromiseLike<L>,
  lm: (l: L) => M | PromiseLike<M>,
  mn: (m: M) => N | PromiseLike<N>,
  no: (n: N) => O | PromiseLike<O>,
  op: (o: O) => P | PromiseLike<P>,
  pq: (p: P) => Q | PromiseLike<Q>,
  qr: (q: Q) => R | PromiseLike<R>,
): Promise<R>

export function pipeAsync<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S>(
  a: A,
  ab: (a: A) => B | PromiseLike<B>,
  bc: (b: B) => C | PromiseLike<C>,
  cd: (c: C) => D | PromiseLike<D>,
  de: (d: D) => E | PromiseLike<E>,
  ef: (e: E) => F | PromiseLike<F>,
  fg: (f: F) => G | PromiseLike<G>,
  gh: (g: G) => H | PromiseLike<H>,
  hi: (h: H) => I | PromiseLike<I>,
  ij: (i: I) => J | PromiseLike<J>,
  jk: (j: J) => K | PromiseLike<K>,
  kl: (k: K) => L | PromiseLike<L>,
  lm: (l: L) => M | PromiseLike<M>,
  mn: (m: M) => N | PromiseLike<N>,
  no: (n: N) => O | PromiseLike<O>,
  op: (o: O) => P | PromiseLike<P>,
  pq: (p: P) => Q | PromiseLike<Q>,
  qr: (q: Q) => R | PromiseLike<R>,
  rs: (r: R) => S | PromiseLike<S>,
): Promise<S>

export function pipeAsync<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T>(
  a: A,
  ab: (a: A) => B | PromiseLike<B>,
  bc: (b: B) => C | PromiseLike<C>,
  cd: (c: C) => D | PromiseLike<D>,
  de: (d: D) => E | PromiseLike<E>,
  ef: (e: E) => F | PromiseLike<F>,
  fg: (f: F) => G | PromiseLike<G>,
  gh: (g: G) => H | PromiseLike<H>,
  hi: (h: H) => I | PromiseLike<I>,
  ij: (i: I) => J | PromiseLike<J>,
  jk: (j: J) => K | PromiseLike<K>,
  kl: (k: K) => L | PromiseLike<L>,
  lm: (l: L) => M | PromiseLike<M>,
  mn: (m: M) => N | PromiseLike<N>,
  no: (n: N) => O | PromiseLike<O>,
  op: (o: O) => P | PromiseLike<P>,
  pq: (p: P) => Q | PromiseLike<Q>,
  qr: (q: Q) => R | PromiseLike<R>,
  rs: (r: R) => S | PromiseLike<S>,
  st: (s: S) => T | PromiseLike<T>,
): Promise<T>

export async function pipeAsync(
  a: unknown,
  ab?: Function,
  bc?: Function,
  cd?: Function,
  de?: Function,
  ef?: Function,
  fg?: Function,
  gh?: Function,
  hi?: Function,
  ij?: Function,
  jk?: Function,
  kl?: Function,
  lm?: Function,
  mn?: Function,
  no?: Function,
  op?: Function,
  pq?: Function,
  qr?: Function,
  rs?: Function,
  st?: Function,
): Promise<unknown> {
  switch (arguments.length) {
    case 1:
      return a
    case 2:
      return ab!(a)
    case 3:
      return bc!(await ab!(a))
    case 4:
      return cd!(await bc!(await ab!(a)))
    case 5:
      return de!(await cd!(await bc!(await ab!(a))))
    case 6:
      return ef!(await de!(await cd!(await bc!(await ab!(a)))))
    case 7:
      return fg!(await ef!(await de!(await cd!(await bc!(await ab!(a))))))
    case 8:
      return gh!(await fg!(await ef!(await de!(await cd!(await bc!(await ab!(a)))))))
    case 9:
      return hi!(await gh!(await fg!(await ef!(await de!(await cd!(await bc!(await ab!(a))))))))
    case 10:
      return ij!(
        await hi!(await gh!(await fg!(await ef!(await de!(await cd!(await bc!(await ab!(a)))))))),
      )
    case 11:
      return jk!(
        await ij!(
          await hi!(await gh!(await fg!(await ef!(await de!(await cd!(await bc!(await ab!(a)))))))),
        ),
      )
    case 12:
      return kl!(
        await jk!(
          await ij!(
            await hi!(
              await gh!(await fg!(await ef!(await de!(await cd!(await bc!(await ab!(a))))))),
            ),
          ),
        ),
      )
    case 13:
      return lm!(
        await kl!(
          await jk!(
            await ij!(
              await hi!(
                await gh!(await fg!(await ef!(await de!(await cd!(await bc!(await ab!(a))))))),
              ),
            ),
          ),
        ),
      )
    case 14:
      return mn!(
        await lm!(
          await kl!(
            await jk!(
              await ij!(
                await hi!(
                  await gh!(await fg!(await ef!(await de!(await cd!(await bc!(await ab!(a))))))),
                ),
              ),
            ),
          ),
        ),
      )
    case 15:
      return no!(
        await mn!(
          await lm!(
            await kl!(
              await jk!(
                await ij!(
                  await hi!(
                    await gh!(await fg!(await ef!(await de!(await cd!(await bc!(await ab!(a))))))),
                  ),
                ),
              ),
            ),
          ),
        ),
      )
    case 16:
      return op!(
        await no!(
          await mn!(
            await lm!(
              await kl!(
                await jk!(
                  await ij!(
                    await hi!(
                      await gh!(
                        await fg!(await ef!(await de!(await cd!(await bc!(await ab!(a)))))),
                      ),
                    ),
                  ),
                ),
              ),
            ),
          ),
        ),
      )
    case 17:
      return pq!(
        await op!(
          await no!(
            await mn!(
              await lm!(
                await kl!(
                  await jk!(
                    await ij!(
                      await hi!(
                        await gh!(
                          await fg!(await ef!(await de!(await cd!(await bc!(await ab!(a)))))),
                        ),
                      ),
                    ),
                  ),
                ),
              ),
            ),
          ),
        ),
      )
    case 18:
      return qr!(
        await pq!(
          await op!(
            await no!(
              await mn!(
                await lm!(
                  await kl!(
                    await jk!(
                      await ij!(
                        await hi!(
                          await gh!(
                            await fg!(await ef!(await de!(await cd!(await bc!(await ab!(a)))))),
                          ),
                        ),
                      ),
                    ),
                  ),
                ),
              ),
            ),
          ),
        ),
      )
    case 19:
      return rs!(
        await qr!(
          await pq!(
            await op!(
              await no!(
                await mn!(
                  await lm!(
                    await kl!(
                      await jk!(
                        await ij!(
                          await hi!(
                            await gh!(
                              await fg!(await ef!(await de!(await cd!(await bc!(await ab!(a)))))),
                            ),
                          ),
                        ),
                      ),
                    ),
                  ),
                ),
              ),
            ),
          ),
        ),
      )
    case 20:
      return st!(
        await rs!(
          await qr!(
            await pq!(
              await op!(
                await no!(
                  await mn!(
                    await lm!(
                      await kl!(
                        await jk!(
                          await ij!(
                            await hi!(
                              await gh!(
                                await fg!(await ef!(await de!(await cd!(await bc!(await ab!(a)))))),
                              ),
                            ),
                          ),
                        ),
                      ),
                    ),
                  ),
                ),
              ),
            ),
          ),
        ),
      )
  }
  return
}
