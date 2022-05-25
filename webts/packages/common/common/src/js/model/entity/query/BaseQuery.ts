import { En_SortDir } from "./En_SortDir"
import { En_SortField } from "./En_SortField"

export interface BaseQuery {
  searchString?: string
  sortField?: En_SortField
  sortDir?: En_SortDir
  limit?: number
  offset?: number
}
