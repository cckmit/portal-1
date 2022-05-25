import { BaseQuery } from "./BaseQuery"

export interface EmployeeQuery extends BaseQuery {
  ids?: Array<number>
  exceptIds?: Array<number>
  fired?: boolean
  deleted?: boolean
  onlyPeople?: boolean
  workPhone?: string
  mobilePhone?: string
  ipAddress?: string
  emailByLike?: string
  departmentParent?: string
  firstName?: string
  lastName?: string
  secondName?: string
  absent?: boolean
}
