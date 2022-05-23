export function addSlashToPath(path: string): string {
  if (!path.startsWith("/")) {
    return "/" + path
  }
  return path
}
