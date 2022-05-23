import { En_ResultStatus } from "../En_ResultStatus"

export interface JsonRequest<T = unknown> {
  requestId: string
  data: T
}
