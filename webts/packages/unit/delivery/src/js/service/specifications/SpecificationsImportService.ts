import _ from "lodash"
import { inject, injectable } from "inversify"
import { ArrayType } from "@protei-libs/types"
import { makeLogger } from "@protei-libs/logger"
import { newException } from "@protei-libs/exception"
import { cleanStore, runInTransaction } from "@protei-libs/store"
import {
  CompanyQuery,
  CompanyTransport,
  CompanyTransport$type,
  CreateDeliveryDetail,
  CreateDeliveryDetailToSpecification,
  CreateDeliveryDetailToSpecificationModification,
  CreateDeliverySpecification,
  CreateDeliverySpecificationToSpecification,
  CreateDeliverySpecificationToSpecificationModification,
  DeliverySpecificationTransport,
  DeliverySpecificationTransport$type,
  detectException,
  distinct,
  EmployeeQuery,
  ExceptionName,
  isNotUndefined,
  isProgressError,
  PersonTransport,
  PersonTransport$type,
  progressError,
  progressProcessing,
  progressReady,
} from "@protei-portal/common"
import { SpecificationsImportStore, SpecificationsImportStore$type } from "../../store"
import { SpecificationsParserXLSX, SpecificationsParserXLSXImpl } from "./parser/SpecificationsParserXLSX"
import { SpecificationXLSXColumnType, SpecificationXLSXRowDetail, SpecificationXLSXRowSpecification } from "../../model"

export const SpecificationsImportService$type = Symbol("SpecificationsImportService")

export interface SpecificationsImportService {
  reset(): void
  hasParseErrors(): boolean
  setName(name: string): void
  parseFileXLSX(file: File): Promise<void>
  import(): Promise<void>
}

@injectable()
export class SpecificationsImportServiceImpl implements SpecificationsImportService {

  reset(): void {
    this.log.info("Reset")
    runInTransaction(() => {
      this.specificationsImportStore.specification = this.makeEmptySpecification()
      this.specificationsImportStore.details = []
      this.specificationsImportStore.progress = progressReady()
      this.specificationsImportStore.parse.errors = []
      this.specificationsImportStore.parse.progress = progressReady()
    })
  }

  hasParseErrors(): boolean {
    return this.specificationsImportStore.parse.errors.length > 0
      || isProgressError(this.specificationsImportStore.parse.progress)
  }

  setName(name: string): void {
    runInTransaction(() => {
      if (!this.specificationsImportStore.specification) {
        this.specificationsImportStore.specification = this.makeEmptySpecification()
      }
      this.specificationsImportStore.specification.name = name
      this.resetImportError()
    })
  }

  async import(): Promise<void> {
    this.log.info("Import")
    const specification = this.specificationsImportStore.specification
    const details = this.specificationsImportStore.details
    if (specification === undefined) {
      this.log.warn("Unable to import specification, no specification defined")
      return
    }
    try {
      runInTransaction(() => {
        this.specificationsImportStore.progress = progressProcessing()
      })
      const imported = await this.deliverySpecificationTransport.import(
        [ cleanStore(specification) ],
        cleanStore(details),
      )
      if (!imported) {
        const message = "Server response is false"
        const exception = newException(ExceptionName.API_ERROR, { message })
        this.log.error("Import | failed", exception)
        runInTransaction(() => {
          this.specificationsImportStore.progress = progressError(exception)
        })
        return
      }
      this.log.info("Import | done")
      runInTransaction(() => {
        this.reset()
        this.specificationsImportStore.progress = progressReady()
      })
    } catch (e) {
      const exception = detectException(e)
      this.log.error("Failed to import specification", exception)
      runInTransaction(() => {
        this.specificationsImportStore.progress = progressError(exception)
      })
      throw exception
    }
  }

  async parseFileXLSX(file: File): Promise<void> {
    try {
      this.log.info("Parse xlsx file")
      runInTransaction(() => {
        if (!this.specificationsImportStore.specification) {
          this.specificationsImportStore.specification = this.makeEmptySpecification()
        }
        this.specificationsImportStore.specification.specifications = []
        this.specificationsImportStore.specification.details = []
        this.specificationsImportStore.details = []
        this.specificationsImportStore.parse.errors = []
        this.specificationsImportStore.parse.progress = progressProcessing()
        this.resetImportError()
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
      const personNames = this.collectPersons(detailRows, specificationRows)
      const companyNames = this.collectCompanies(detailRows, specificationRows)
      const [ persons, companies ] = await Promise.all([
        this.fetchPersonIds(personNames),
        this.fetchCompanyIds(companyNames),
      ])
      const specifications = this.makeSpecifications(specificationRows, persons, companies, onError)
      const details = this.makeDetails(detailRows, persons, companies, onError)
      this.log.info("Import xlsx file | details={}, specifications={}, errors={}", details.length, specifications.length, errors.length)
      runInTransaction(() => {
        if (!this.specificationsImportStore.specification) {
          this.specificationsImportStore.specification = this.makeEmptySpecification()
        }
        this.specificationsImportStore.specification.specifications.push(...specifications)
        this.specificationsImportStore.specification.details.push(...details.map(d => d.detail2spec))
        this.specificationsImportStore.details.push(...details.map(d => d.detail))
        this.specificationsImportStore.parse.errors.push(...errors)
        this.specificationsImportStore.parse.progress = progressReady()
      })
    } catch (e) {
      const exception = detectException(e)
      this.log.error("Failed to parse xlsx file", exception)
      runInTransaction(() => {
        this.specificationsImportStore.parse.progress = progressError(exception)
      })
      throw exception
    }
  }

  private resetImportError(): void {
    runInTransaction(() => {
      if (isProgressError(this.specificationsImportStore.progress)) {
        this.specificationsImportStore.progress = progressReady()
      }
    })
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
    return Promise.all(_.chunk(names, 10)
      .map(chunk => Promise.all(chunk
        .map(name => {
          const query: EmployeeQuery = {
            searchString: name,
          }
          return this.personTransport.getPersonShortViewListByQuery(query)
            .then(persons => persons[0])
            .then(person => ({
              name: name,
              id: person?.id,
            }))
        }),
      )),
    )
      .then(chunks => chunks.flat())
  }

  private async fetchCompanyIds(names: Array<string>): Promise<Array<{ name: string, id: number | undefined }>> {
    return Promise.all(_.chunk(names, 10)
      .map(chunk => Promise.all(chunk
        .map(name => {
          const query: CompanyQuery = {
            searchString: name,
          }
          return this.companyTransport.getCompanyOptionListByQuery(query)
            .then(companies => companies[0])
            .then(company => ({
              name: name,
              id: company?.id,
            }))
        }),
      )),
    )
      .then(chunks => chunks.flat())
  }

  private makeSpecifications(
    specificationRows: Array<SpecificationXLSXRowSpecification>,
    persons: Array<{ name: string, id: number | undefined }>,
    companies: Array<{ name: string, id: number | undefined }>,
    onError: (message: string) => void,
  ): Array<CreateDeliverySpecificationToSpecification> {
    return specificationRows
      .map<CreateDeliverySpecificationToSpecification | undefined>(row => {
        const specificationId = row[SpecificationXLSXColumnType.specificationId].value
        const category = row[SpecificationXLSXColumnType.category].value
        const modifications = row[SpecificationXLSXColumnType.modificationN]
          .map<CreateDeliverySpecificationToSpecificationModification>(modification => ({
            number: modification.number,
            count: modification.count,
          }))
        return {
          childSpecificationId: specificationId,
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
  ): Array<{ detail2spec: CreateDeliveryDetailToSpecification, detail: CreateDeliveryDetail }> {
    return detailRows
      .map<undefined | ArrayType<ReturnType<typeof this.makeDetails>>>(row => {
        const article = row[SpecificationXLSXColumnType.article]?.value
        const name = row[SpecificationXLSXColumnType.name].value
        const responsible = row[SpecificationXLSXColumnType.responsible].value
        const responsibleId = persons.find(person => person.name === responsible)?.id
        const supplier = row[SpecificationXLSXColumnType.supplier].value
        const supplierId = companies.find(company => company.name === supplier)?.id
        const configuration = row[SpecificationXLSXColumnType.configuration]?.value
        const color = row[SpecificationXLSXColumnType.color]?.value
        const reserve = row[SpecificationXLSXColumnType.reserve]?.value
        const category = row[SpecificationXLSXColumnType.category].value
        const simplified = row[SpecificationXLSXColumnType.simplified]?.value ?? false
        const attn = row[SpecificationXLSXColumnType.attn]?.value ?? false
        const componentType = row[SpecificationXLSXColumnType.componentType]?.value
        const value = row[SpecificationXLSXColumnType.value]?.value
        const dateModified = row[SpecificationXLSXColumnType.dateModified]?.value
        const note = row[SpecificationXLSXColumnType.note]?.value
        const partReference = row[SpecificationXLSXColumnType.partReference]?.value
        const modifications = row[SpecificationXLSXColumnType.modificationN]
          .map<CreateDeliveryDetailToSpecificationModification>(modification => ({
            number: modification.number,
            count: modification.count,
          }))
        if (responsibleId === undefined) {
          const message = `${row[SpecificationXLSXColumnType.responsible].addressCol}${row.addressRow} - колонка содержит имя, которое не удалось найти в системе: '${responsible}'`
          onError(message)
          return undefined
        }
        if (supplierId === undefined) {
          const message = `${row[SpecificationXLSXColumnType.supplier].addressCol}${row.addressRow} - колонка содержит название, которое не удалось найти в системе: '${supplier}'`
          onError(message)
          return undefined
        }
        const detailId = this.syntheticDetailId--
        return {
          detail2spec: {
            detailId: detailId,
            dateModified: dateModified,
            note: note,
            partReference: partReference,
            modifications: modifications,
          },
          detail: {
            id: detailId,
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
        }
      })
      .filter(isNotUndefined)
  }

  constructor(
    @inject(SpecificationsImportStore$type) specificationsImportStore: SpecificationsImportStore,
    @inject(DeliverySpecificationTransport$type) deliverySpecificationTransport: DeliverySpecificationTransport,
    @inject(PersonTransport$type) personTransport: PersonTransport,
    @inject(CompanyTransport$type) companyTransport: CompanyTransport,
  ) {
    this.specificationsImportStore = specificationsImportStore
    this.deliverySpecificationTransport = deliverySpecificationTransport
    this.personTransport = personTransport
    this.companyTransport = companyTransport
  }

  private syntheticDetailId = -1
  private readonly specificationsImportStore: SpecificationsImportStore
  private readonly deliverySpecificationTransport: DeliverySpecificationTransport
  private readonly personTransport: PersonTransport
  private readonly companyTransport: CompanyTransport
  private readonly log = makeLogger("portal.delivery.spec.import")
}
