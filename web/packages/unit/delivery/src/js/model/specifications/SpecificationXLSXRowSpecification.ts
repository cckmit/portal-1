import { SpecificationXLSXColumnType } from "./SpecificationXLSXColumnType"

export interface SpecificationXLSXRowSpecification {
  [SpecificationXLSXColumnType.specificationId]: {
    value: number
  }
  [SpecificationXLSXColumnType.category]: {
    value: number
  }
  [SpecificationXLSXColumnType.modificationN]: Array<{
    number: number
    count: number
  }>
}
