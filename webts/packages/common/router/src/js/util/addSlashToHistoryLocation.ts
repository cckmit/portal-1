import { Location } from "history"

export function addSlashToHistoryLocation(location: Location): Location {
  if (!location.pathname.startsWith("/")) {
    return {
      ...location,
      pathname: "/" + location.pathname,
    }
  }
  return location
}
