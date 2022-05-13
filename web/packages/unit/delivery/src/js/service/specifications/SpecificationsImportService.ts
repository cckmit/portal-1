import { inject, injectable } from "inversify"
import { makeLogger } from "@protei-libs/logger"
import { runInTransaction } from "@protei-libs/store"
import { distinct } from "@protei-portal/common"
import { SpecificationsCreateStore, SpecificationsCreateStore$type } from "../../store"
import { SpecificationsParserXLSX, SpecificationsParserXLSXImpl } from "../../util/SpecificationsParserXLSX"
import { SpecificationXLSXColumnType, SpecificationXLSXRowDetail, SpecificationXLSXRowSpecification } from "../../model"

export const SpecificationsImportService$type = Symbol("SpecificationsImportService")

export interface SpecificationsImportService {
  importFileXLSX(file: File): Promise<void>
}

@injectable()
export class SpecificationsImportServiceImpl implements SpecificationsImportService {

  async importFileXLSX(file: File): Promise<void> {
    this.log.info("Import xlsx file")
    runInTransaction(() => {
      if (this.specificationsCreateStore.specification) {
        this.specificationsCreateStore.specification.specifications = []
        this.specificationsCreateStore.specification.details = []
      }
    })
    const detailRows: Array<SpecificationXLSXRowDetail> = []
    const specificationRows: Array<SpecificationXLSXRowSpecification> = []
    const errors: Array<string> = []
    const parser = this.makeParser()
    await parser.parse(
      file,
      (row) => detailRows.push(row),
      (row) => specificationRows.push(row),
      (message) => errors.push(message),
    )
    this.log.info("Import xlsx file | details={}, specifications={}, errors={}", detailRows.length, specificationRows.length, errors.length)
    const personNames = this.collectPersons(detailRows, specificationRows)
    const companyNames = this.collectCompanies(detailRows, specificationRows)
    const [ persons, companies ] = await Promise.all([
      this.fetchPersonIds(personNames),
      this.fetchCompanyIds(companyNames),
    ])

    console.log("SSS details", detailRows)
    console.log("SSS specifications", specificationRows)
    console.log("SSS errors", errors)
    console.log("SSS personNames", personNames)
    console.log("SSS companyNames", companyNames)
  }

  private makeParser(): SpecificationsParserXLSX {
    return new SpecificationsParserXLSXImpl()
  }

  private collectPersons(detailRows: Array<SpecificationXLSXRowDetail>, specificationRows: Array<SpecificationXLSXRowSpecification>): Array<string> {
    return detailRows
      .map(detail => detail[SpecificationXLSXColumnType.responsible].value)
      .filter(distinct)
  }

  private collectCompanies(detailRows: Array<SpecificationXLSXRowDetail>, specificationRows: Array<SpecificationXLSXRowSpecification>): Array<string> {
    return detailRows
      .map(detail => detail[SpecificationXLSXColumnType.supplier].value)
      .filter(distinct)
  }

  private async fetchPersonIds(names: Array<string>): Promise<Array<{ name: string, id: number | undefined }>> {
    // TODO api request
    return []
  }

  private async fetchCompanyIds(names: Array<string>): Promise<Array<{ name: string, id: number | undefined }>> {
    // TODO api request
    return []
  }

  constructor(
    @inject(SpecificationsCreateStore$type) specificationsCreateStore: SpecificationsCreateStore,
  ) {
    this.specificationsCreateStore = specificationsCreateStore
  }

  private readonly specificationsCreateStore: SpecificationsCreateStore
  private readonly log = makeLogger("portal.delivery.spec.import")
}
