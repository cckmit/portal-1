import { inject, injectable } from "inversify"
import { makeLogger } from "@protei-libs/logger"
import { runInTransaction } from "@protei-libs/store"
import {
  CompanyQuery,
  CompanyTransport,
  CompanyTransport$type,
  CreateDeliveryDetailAtSpecification,
  CreateDeliveryDetailAtSpecificationModification,
  CreateDeliverySpecification,
  CreateDeliverySpecificationAtSpecification,
  CreateDeliverySpecificationAtSpecificationModification,
  detectException,
  distinct,
  EmployeeQuery,
  isNotUndefined,
  PersonTransport,
  PersonTransport$type,
  progressError,
  progressProcessing,
  progressReady,
} from "@protei-portal/common"
import { SpecificationsCreateStore, SpecificationsCreateStore$type } from "../../store"
import { SpecificationsParserXLSX, SpecificationsParserXLSXImpl } from "./parser/SpecificationsParserXLSX"
import { SpecificationXLSXColumnType, SpecificationXLSXRowDetail, SpecificationXLSXRowSpecification } from "../../model"

export const SpecificationsImportService$type = Symbol("SpecificationsImportService")

export interface SpecificationsImportService {
  setName(name: string): void

  importFileXLSX(file: File): Promise<void>
}

@injectable()
export class SpecificationsImportServiceImpl implements SpecificationsImportService {

  setName(name: string): void {
    this.log.info("Set name as '{}'", name)
    runInTransaction(() => {
      if (!this.specificationsCreateStore.specification) {
        this.specificationsCreateStore.specification = this.makeEmptySpecification()
      }
      this.specificationsCreateStore.specification.name = name
    })
  }

  async importFileXLSX(file: File): Promise<void> {
    try {
      this.log.info("Import xlsx file")
      runInTransaction(() => {
        if (!this.specificationsCreateStore.specification) {
          this.specificationsCreateStore.specification = this.makeEmptySpecification()
        }
        this.specificationsCreateStore.specification.specifications = []
        this.specificationsCreateStore.specification.details = []
        this.specificationsCreateStore.errors = []
        this.specificationsCreateStore.progress = progressProcessing()
      })
      const detailRows: Array<SpecificationXLSXRowDetail> = []
      const specificationRows: Array<SpecificationXLSXRowSpecification> = []
      const errors: Array<string> = []
      const onError = (message: string) => errors.push(message)
      const parser = this.makeParser()
      await parser.parse(
        file,
        (row) => detailRows.push(row),
        (row) => specificationRows.push(row),
        onError,
      )
      this.log.info("Import xlsx file | details={}, specifications={}, errors={}", detailRows.length, specificationRows.length, errors.length)
      const personNames = this.collectPersons(detailRows, specificationRows)
      const companyNames = this.collectCompanies(detailRows, specificationRows)
      const [ persons, companies ] = await Promise.all([
        this.fetchPersonIds(personNames),
        this.fetchCompanyIds(companyNames),
      ])
      const specifications = this.makeSpecifications(specificationRows, persons, companies, onError)
      const details = this.makeDetails(detailRows, persons, companies, onError)
      runInTransaction(() => {
        if (this.specificationsCreateStore.specification) {
          this.specificationsCreateStore.specification.specifications.push(...specifications)
          this.specificationsCreateStore.specification.details.push(...details)
          this.specificationsCreateStore.errors.push(...errors)
          this.specificationsCreateStore.progress = progressReady()
        }
      })
    } catch (e) {
      const exception = detectException(e)
      this.log.error("Failed to import xlsx file", exception)
      runInTransaction(() => {
        this.specificationsCreateStore.progress = progressError(exception)
      })
      throw exception
    }
  }

  private makeEmptySpecification(): CreateDeliverySpecification {
    return {
      name: "",
      specifications: [],
      details: [],
    }
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
    return Promise.all(names.map(name => {
      const query: EmployeeQuery = {
        searchString: name,
      }
      return this.personTransport.getPersonShortViewListByQuery(query)
        .then(persons => persons[0])
        .then(person => ({
          name: name,
          id: person?.id,
        }))
    }))
  }

  private async fetchCompanyIds(names: Array<string>): Promise<Array<{ name: string, id: number | undefined }>> {
    return Promise.all(names.map(name => {
      const query: CompanyQuery = {
        searchString: name,
      }
      return this.companyTransport.getCompanyOptionListByQuery(query)
        .then(companies => companies[0])
        .then(company => ({
          name: name,
          id: company?.id,
        }))
    }))
  }

  private makeSpecifications(
    specificationRows: Array<SpecificationXLSXRowSpecification>,
    persons: Array<{ name: string, id: number | undefined }>,
    companies: Array<{ name: string, id: number | undefined }>,
    onError: (message: string) => void,
  ): Array<CreateDeliverySpecificationAtSpecification> {
    return specificationRows
      .map<CreateDeliverySpecificationAtSpecification | undefined>(row => {
        const specificationId = row[SpecificationXLSXColumnType.specificationId].value
        const category = row[SpecificationXLSXColumnType.category].value
        const modifications = row[SpecificationXLSXColumnType.modificationN]
          .map<CreateDeliverySpecificationAtSpecificationModification>(modification => ({
            number: modification.number,
            count: modification.count,
          }))
        return {
          specification: undefined,
          specificationId: specificationId,
          category: category,
          modifications: modifications,
        }
      })
      .filter(isNotUndefined)
  }

  private makeDetails(
    detailRows: Array<SpecificationXLSXRowDetail>,
    persons: Array<{ name: string, id: number | undefined }>,
    companies: Array<{ name: string, id: number | undefined }>,
    onError: (message: string) => void,
  ): Array<CreateDeliveryDetailAtSpecification> {
    return detailRows
      .map<CreateDeliveryDetailAtSpecification | undefined>(row => {
        const article = row[SpecificationXLSXColumnType.article].value
        const name = row[SpecificationXLSXColumnType.name].value
        const responsible = row[SpecificationXLSXColumnType.responsible].value
        const responsibleId = persons.find(person => person.name === responsible)?.id
        const supplier = row[SpecificationXLSXColumnType.supplier].value
        const supplierId = companies.find(company => company.name === supplier)?.id
        const configuration = row[SpecificationXLSXColumnType.configuration].value
        const color = row[SpecificationXLSXColumnType.color].value
        const reserve = row[SpecificationXLSXColumnType.reserve].value
        const category = row[SpecificationXLSXColumnType.category].value
        const simplified = row[SpecificationXLSXColumnType.simplified].value
        const attn = row[SpecificationXLSXColumnType.attn].value
        const componentType = row[SpecificationXLSXColumnType.componentType].value
        const value = row[SpecificationXLSXColumnType.value].value
        const dateModified = row[SpecificationXLSXColumnType.dateModified].value
        const note = row[SpecificationXLSXColumnType.note].value
        const partReference = row[SpecificationXLSXColumnType.partReference].value
        const modifications = row[SpecificationXLSXColumnType.modificationN]
          .map<CreateDeliveryDetailAtSpecificationModification>(modification => ({
            number: modification.number,
            count: modification.count,
          }))
        if (responsibleId === undefined) {
          const message = `${row.addressRow} - колонка '${SpecificationXLSXColumnType.responsible}' содержит имя, которое не удалось найти в системе`
          onError(message)
          return undefined
        }
        if (supplierId === undefined) {
          const message = `${row.addressRow} - колонка '${SpecificationXLSXColumnType.supplier}' содержит название, которое не удалось найти в системе`
          onError(message)
          return undefined
        }
        return {
          detail: {
            article: article,
            name: name,
            responsibleId: responsibleId,
            supplierId: supplierId,
            configuration: configuration,
            color: color,
            reserve: reserve,
            category: category,
            simplified: simplified,
            attn: attn,
            componentType: componentType,
            value: value,
          },
          detailId: undefined,
          dateModified: dateModified,
          note: note,
          partReference: partReference,
          modifications: modifications,
        }
      })
      .filter(isNotUndefined)
  }

  constructor(
    @inject(SpecificationsCreateStore$type) specificationsCreateStore: SpecificationsCreateStore,
    @inject(PersonTransport$type) personTransport: PersonTransport,
    @inject(CompanyTransport$type) companyTransport: CompanyTransport,
  ) {
    this.specificationsCreateStore = specificationsCreateStore
    this.personTransport = personTransport
    this.companyTransport = companyTransport
  }

  private readonly specificationsCreateStore: SpecificationsCreateStore
  private readonly personTransport: PersonTransport
  private readonly companyTransport: CompanyTransport
  private readonly log = makeLogger("portal.delivery.spec.import")
}
