import { Exception } from "@protei-libs/exception"
import { makeLogger } from "@protei-libs/logger"

export const log = makeLogger("ewc.exception")

export function handleException(exception: Exception, text?: { header?: string, description?: string }): void {
  // TODO show exception notification, show log for now
  log.error("Exception occurred", exception)
}
