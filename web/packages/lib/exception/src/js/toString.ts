import { isException } from "./isException"

type NameMapper = (name: string) => string

export const exceptionToDescString = (error: Error, nameMapper?: NameMapper): string =>
  toString(error, false, false, nameMapper)

export const exceptionToShortString = (error: Error, nameMapper?: NameMapper): string =>
  toString(error, true, false, nameMapper)

export const exceptionToString = (error: Error, nameMapper?: NameMapper): string =>
  toString(error, true, true, nameMapper)

function toString(
  error: Error | unknown,
  withCause: boolean,
  withStack: boolean,
  nameMapper?: NameMapper,
): string {
  if (!(error instanceof Error) && !isException(error)) {
    return `Not instance of error: ${error}`
  }
  const name = nameMapper?.(error.name) || error.name
  const description = name !== error.message ? [`${name}: ${error.message}`] : [name]
  if (withStack && error.stack) {
    const stack = error.stack
      .split("\n")
      .map((line) => `  ${line}`)
      .join("\n")
    description.push(stack)
  }
  if (withCause && isException(error) && error.cause) {
    description.push(`Caused by: ${toString(error.cause, withCause, withStack)}`)
  }
  return description.join("\n")
}
