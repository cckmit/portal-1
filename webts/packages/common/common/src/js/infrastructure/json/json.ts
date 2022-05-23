export type JsonPlugin = {
  parse: (key: string, value: unknown) => unknown
  stringify: (key: string, value: unknown) => unknown
}

export type Json = {
  parse: (
    text: string,
    reviver?: (key: string, value: unknown) => unknown,
  ) => unknown
  stringify: (
    value: unknown,
    replacer?: (key: string, value: unknown) => unknown,
    space?: string | number,
  ) => string
}

export function makeJson(plugins: Array<JsonPlugin>): Json {
  const parsers = plugins.map(plugin => plugin.parse)
  const stringifies = plugins.map(plugin => plugin.stringify)

  return {
    parse: (
      text: string,
      reviver?: (key: string, value: unknown) => unknown,
    ): unknown => {
      return parse(parsers, text, reviver)
    },
    stringify: (
      value: unknown,
      replacer?: (key: string, value: unknown) => unknown,
      space?: string | number,
    ): string => {
      return stringify(stringifies, value, replacer, space)
    },
  }
}

function parse(parsers: Array<JsonPlugin["parse"]>, text: string, reviver?: (key: string, value: unknown) => unknown): unknown {
  return JSON.parse(text, (key, value) => {
    for (const parser of parsers) {
      value = parser(key, value)
    }
    if (typeof reviver === "function") {
      return reviver(key, value)
    }
    // eslint-disable-next-line @typescript-eslint/no-unsafe-return
    return value
  })
}

function stringify(stringifies: Array<JsonPlugin["stringify"]>, value: unknown, replacer?: (key: string, value: unknown) => unknown, space?: string | number): string {
  return JSON.stringify(value, (key, value) => {
    for (const stringifier of stringifies) {
      value = stringifier(key, value)
    }
    if (typeof replacer === "function") {
      return replacer(key, value)
    }
    // eslint-disable-next-line @typescript-eslint/no-unsafe-return
    return value
  }, space)
}
