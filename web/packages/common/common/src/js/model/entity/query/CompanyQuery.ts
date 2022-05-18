import { BaseQuery } from "./BaseQuery"

export interface CompanyQuery extends BaseQuery {
  categoryIds?: Array<number>
  synchronizeWith1C?: boolean
  isOnlyParentCompanies?: boolean
  sortHomeCompaniesAtBegin?: boolean
  onlyVisibleFields?: boolean
  companyIds?: Array<number>
  isShowDeprecated?: boolean
  isReverseOrder?: boolean
  isShowHidden?: boolean
}
