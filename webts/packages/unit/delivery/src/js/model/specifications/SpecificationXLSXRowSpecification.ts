import { SpecificationXLSXColumnType } from "./SpecificationXLSXColumnType"

export interface SpecificationXLSXRowSpecification {
  addressRow: string
  [SpecificationXLSXColumnType.specificationId]: {
    addressCol: string
    value: number
  }
  [SpecificationXLSXColumnType.category]: {
    addressCol: string
    value: number
  }
  [SpecificationXLSXColumnType.modificationN]: Array<{
    addressCol: string
    number: number
    count: number
  }>
}
