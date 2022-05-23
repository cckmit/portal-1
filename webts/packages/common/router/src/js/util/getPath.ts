import { Path } from "../model/Path"
import { addSlashToPath } from "./addSlashToPath"

export function getPath(path: Path): string {
  const url = path.path
  return addSlashToPath(url)
}
