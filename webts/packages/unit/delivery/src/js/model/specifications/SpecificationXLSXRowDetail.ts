import { SpecificationXLSXColumnType } from "./SpecificationXLSXColumnType"

export interface SpecificationXLSXRowDetail {
  addressRow: string
  [SpecificationXLSXColumnType.article]: undefined | {
    addressCol: string
    value: string
  }
  [SpecificationXLSXColumnType.name]: {
    addressCol: string
    value: string
  }
  [SpecificationXLSXColumnType.responsible]: {
    addressCol: string
    value: string
  }
  [SpecificationXLSXColumnType.supplier]: {
    addressCol: string
    value: string
  }
  [SpecificationXLSXColumnType.configuration]: undefined | {
    addressCol: string
    value: string
  }
  [SpecificationXLSXColumnType.color]: undefined | {
    addressCol: string
    value: string
  }
  [SpecificationXLSXColumnType.reserve]: undefined | {
    addressCol: string
    value: number
  }
  [SpecificationXLSXColumnType.category]: {
    addressCol: string
    value: number
  }
  [SpecificationXLSXColumnType.simplified]: undefined | {
    addressCol: string
    value: boolean
  }
  [SpecificationXLSXColumnType.attn]: undefined | {
    addressCol: string
    value: boolean
  }
  [SpecificationXLSXColumnType.componentType]: undefined | {
    addressCol: string
    value: string
  }
  [SpecificationXLSXColumnType.value]: undefined | {
    addressCol: string
    value: string
  }
  [SpecificationXLSXColumnType.dateModified]: undefined | {
    addressCol: string
    value: Date
  }
  [SpecificationXLSXColumnType.note]: undefined | {
    addressCol: string
    value: string
  }
  [SpecificationXLSXColumnType.partReference]: undefined | {
    addressCol: string
    value: string
  }
  [SpecificationXLSXColumnType.modificationN]: Array<{
    addressCol: string
    number: number
    count: number
  }>
}
