export type Path = {
  path: string
  c: {
    [key: string]: Path
  }
}
