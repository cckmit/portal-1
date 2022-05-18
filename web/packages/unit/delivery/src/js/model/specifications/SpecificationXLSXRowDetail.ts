import { SpecificationXLSXColumnType } from "./SpecificationXLSXColumnType"

export interface SpecificationXLSXRowDetail {
  addressRow: string
  [SpecificationXLSXColumnType.article]: {
    value: string | undefined
  }
  [SpecificationXLSXColumnType.name]: {
    value: string
  }
  [SpecificationXLSXColumnType.responsible]: {
    value: string
  }
  [SpecificationXLSXColumnType.supplier]: {
    value: string
  }
  [SpecificationXLSXColumnType.configuration]: {
    value: string | undefined
  }
  [SpecificationXLSXColumnType.color]: {
    value: string | undefined
  }
  [SpecificationXLSXColumnType.reserve]: {
    value: number | undefined
  }
  [SpecificationXLSXColumnType.category]: {
    value: number
  }
  [SpecificationXLSXColumnType.simplified]: {
    value: boolean
  }
  [SpecificationXLSXColumnType.attn]: {
    value: boolean | undefined
  }
  [SpecificationXLSXColumnType.componentType]: {
    value: string | undefined
  }
  [SpecificationXLSXColumnType.value]: {
    value: string | undefined
  }
  [SpecificationXLSXColumnType.dateModified]: {
    value: Date | undefined
  }
  [SpecificationXLSXColumnType.note]: {
    value: string | undefined
  }
  [SpecificationXLSXColumnType.partReference]: {
    value: string | undefined
  }
  [SpecificationXLSXColumnType.modificationN]: Array<{
    number: number
    count: number
  }>
}
