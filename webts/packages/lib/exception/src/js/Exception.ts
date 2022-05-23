export interface Exception extends Error {
  _tag: "Exception"
  name: string
  message: string
  cause?: Error | unknown
  stack?: string
}
