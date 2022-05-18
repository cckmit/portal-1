import { CellObject, read, utils, WorkBook, WorkSheet } from "xlsx"
import { promisify, scheduleMacroTask } from "@protei-libs/scheduler"
import { isExceptionNamed, newException } from "@protei-libs/exception"
import { detectException, ExceptionName, isNotUndefined } from "@protei-portal/common"
import { SpecificationXLSXColumnType, SpecificationXLSXRowDetail, SpecificationXLSXRowSpecification } from "../../../model"

export interface SpecificationsParserXLSX {
  parse(
    file: File,
    onRowDetail: (row: SpecificationXLSXRowDetail) => void,
    onRowSpecification: (row: SpecificationXLSXRowSpecification) => void,
    onError: (message: string) => void,
  ): Promise<void>
}

export class SpecificationsParserXLSXImpl implements SpecificationsParserXLSX {

  async parse(
    file: File,
    onRowDetail: (row: SpecificationXLSXRowDetail) => void,
    onRowSpecification: (row: SpecificationXLSXRowSpecification) => void,
    onError: (message: string) => void,
  ): Promise<void> {
    const workbook = await this.readWorkbook(file)
    await this.readSheetEach(workbook, async (sheet) => {
      await this.yield()
      const maxColumnLetter = this.readMaxColumnLetter(workbook, sheet)
      const columns = this.readColumns(workbook, sheet, maxColumnLetter)
      await this.readRowEach(workbook, sheet, maxColumnLetter, async (row) => {
        try {
          if (this.isRowSpecificationAtSpecification(columns, row)) {
            const rowSpecification = this.readRowSpecification(columns, row)
            if (rowSpecification) {
              onRowSpecification(rowSpecification)
            }
          } else {
            const rowDetail = this.readRowDetail(columns, row)
            if (rowDetail) {
              onRowDetail(rowDetail)
            }
          }
        } catch (e) {
          const exception = detectException(e)
          if (isExceptionNamed(exception, ExceptionName.IMPORT_XLSX_PARSE)) {
            onError(exception.message)
          } else {
            throw exception
          }
        }
      })
    })
  }

  private async readWorkbook(file: File): Promise<WorkBook> {
    try {
      const arrayBuffer = await file.arrayBuffer()
      const workbook = read(arrayBuffer)
      if (!workbook) {
        const message = "Failed to get workbook of xlsx file"
        throw newException(ExceptionName.IMPORT_XLSX_PARSE, {message})
      }
      return workbook
    } catch (e) {
      const exception = detectException(e)
      if (isExceptionNamed(exception, ExceptionName.NATIVE)) {
        const message = "Failed to get workbook of xlsx file"
        throw newException(ExceptionName.IMPORT_XLSX_PARSE, {message, cause: exception})
      }
      throw exception
    }
  }

  private async readSheetEach(workbook: WorkBook, fn: (sheet: Sheet) => Promise<void>): Promise<void> {
    const firstSheetName = workbook.SheetNames[0]
    if (!firstSheetName) {
      const message = "Failed to get first sheet of xlsx file, no sheets defined"
      throw newException(ExceptionName.IMPORT_XLSX_PARSE, {message})
    }
    const firstSheet = workbook.Sheets[firstSheetName]
    if (!firstSheet) {
      const message = `Failed to get first sheet of xlsx file, sheet not found for name '${firstSheetName}'`
      throw newException(ExceptionName.IMPORT_XLSX_PARSE, {message})
    }
    await fn({
      name: firstSheetName,
      value: firstSheet,
    })
  }

  private readMaxColumnLetter(workbook: WorkBook, sheet: Sheet): string {
    const range = sheet.value["!ref"]
    if (!range) {
      return "Z"
    }
    return utils.encode_col(utils.decode_range(range).e.c)
  }

  private readColumns(workbook: WorkBook, sheet: Sheet, maxColumnLetter: string): Array<Column> {
    const rowNumber = this.ROW_NUMBER_HEADER
    const row = this.readRow(workbook, sheet, maxColumnLetter, rowNumber)
    const columns: Array<Column> = []
    for (const cell of row.value) {
      const column = this.detectColumn(cell)
      if (column) {
        columns.push(column)
      }
    }
    return columns
  }

  private async readRowEach(workbook: WorkBook, sheet: Sheet, maxColumnLetter: string, fn: (row: Row) => Promise<void>): Promise<void> {
    for (let rowNumber = this.ROW_NUMBER_START_CONTENT; ; rowNumber++) {
      const rowNumberFromZero = rowNumber - this.ROW_NUMBER_START_CONTENT
      const shouldYield = rowNumberFromZero > 0 && rowNumberFromZero % 100 === 0
      if (shouldYield) {
        await this.yield()
      }
      const row = this.readRow(workbook, sheet, maxColumnLetter, rowNumber)
      if (row.value.length === 0) {
        break
      }
      await fn(row)
    }
  }

  private readRow(workbook: WorkBook, sheet: Sheet, maxColumnLetter: string, rowNumber: number): Row {
    const cells: Array<Cell> = []
    const rowNumberString = rowNumber.toFixed()
    const firstLetter = "A".charCodeAt(0)
    const lastLetter = maxColumnLetter.charCodeAt(0)
    for (let i = firstLetter; i <= lastLetter; i++) {
      const columnLetter = String.fromCharCode(i)
      const cellName = columnLetter + rowNumberString
      const cell = sheet.value[cellName]
      if (cell == undefined) {
        continue
      }
      cells.push({
        name: cellName,
        address: {
          column: columnLetter,
          row: rowNumberString,
        },
        value: cell,
      })
    }
    return {
      addressRow: rowNumberString,
      value: cells,
    }
  }

  private readRowDetail(columns: Array<Column>, row: Row): SpecificationXLSXRowDetail | undefined {
    return {
      addressRow: row.addressRow,
      [SpecificationXLSXColumnType.article]: {
        value: this.readCellContent(columns, row, SpecificationXLSXColumnType.article, this.readCellString.bind(this)),
      },
      [SpecificationXLSXColumnType.name]: {
        value: this.readCellContent(columns, row, SpecificationXLSXColumnType.name, this.readCellString.bind(this), true),
      },
      [SpecificationXLSXColumnType.responsible]: {
        value: this.readCellContent(columns, row, SpecificationXLSXColumnType.responsible, this.readCellString.bind(this), true),
      },
      [SpecificationXLSXColumnType.supplier]: {
        value: this.readCellContent(columns, row, SpecificationXLSXColumnType.supplier, this.readCellString.bind(this), true),
      },
      [SpecificationXLSXColumnType.configuration]: {
        value: this.readCellContent(columns, row, SpecificationXLSXColumnType.configuration, this.readCellString.bind(this)),
      },
      [SpecificationXLSXColumnType.color]: {
        value: this.readCellContent(columns, row, SpecificationXLSXColumnType.color, this.readCellString.bind(this)),
      },
      [SpecificationXLSXColumnType.reserve]: {
        value: this.readCellContent(columns, row, SpecificationXLSXColumnType.reserve, this.readCellNumber.bind(this)),
      },
      [SpecificationXLSXColumnType.category]: {
        value: this.readCellContent(columns, row, SpecificationXLSXColumnType.category, this.readCellNumber.bind(this), true),
      },
      [SpecificationXLSXColumnType.simplified]: {
        value: this.readCellContent(columns, row, SpecificationXLSXColumnType.simplified, this.readCellBoolean.bind(this)) ?? false,
      },
      [SpecificationXLSXColumnType.attn]: {
        value: this.readCellContent(columns, row, SpecificationXLSXColumnType.attn, this.readCellBoolean.bind(this)),
      },
      [SpecificationXLSXColumnType.componentType]: {
        value: this.readCellContent(columns, row, SpecificationXLSXColumnType.componentType, this.readCellString.bind(this)),
      },
      [SpecificationXLSXColumnType.value]: {
        value: this.readCellContent(columns, row, SpecificationXLSXColumnType.value, this.readCellString.bind(this)),
      },
      [SpecificationXLSXColumnType.dateModified]: {
        value: this.readCellContent(columns, row, SpecificationXLSXColumnType.dateModified, this.readCellDate.bind(this)),
      },
      [SpecificationXLSXColumnType.note]: {
        value: this.readCellContent(columns, row, SpecificationXLSXColumnType.note, this.readCellString.bind(this)),
      },
      [SpecificationXLSXColumnType.partReference]: {
        value: this.readCellContent(columns, row, SpecificationXLSXColumnType.partReference, this.readCellString.bind(this)),
      },
      [SpecificationXLSXColumnType.modificationN]:
        this.readCellContentModifications(columns, row, SpecificationXLSXColumnType.modificationN),
    }
  }

  private readRowSpecification(columns: Array<Column>, row: Row): SpecificationXLSXRowSpecification | undefined {
    return {
      addressRow: row.addressRow,
      [SpecificationXLSXColumnType.specificationId]: {
        value: this.readCellContent(columns, row, SpecificationXLSXColumnType.specificationId, this.readCellNumber.bind(this), true),
      },
      [SpecificationXLSXColumnType.category]: {
        value: this.readCellContent(columns, row, SpecificationXLSXColumnType.category, this.readCellNumber.bind(this), true),
      },
      [SpecificationXLSXColumnType.modificationN]:
        this.readCellContentModifications(columns, row, SpecificationXLSXColumnType.modificationN),
    }
  }

  private readCellContent<T>(
    columns: Array<Column>,
    row: Row,
    type: SpecificationXLSXColumnType,
    reader: (cell: Cell | undefined) => T | undefined,
    required: true,
  ): T
  private readCellContent<T>(
    columns: Array<Column>,
    row: Row,
    type: SpecificationXLSXColumnType,
    reader: (cell: Cell | undefined) => T | undefined,
    required?: false,
  ): T | undefined
  private readCellContent<T>(
    columns: Array<Column>,
    row: Row,
    type: SpecificationXLSXColumnType,
    reader: (cell: Cell | undefined) => T | undefined,
    required?: boolean,
  ): T | undefined {
    const cell = this.getCellForType(columns, row, type)
    if (!cell) {
      if (required) {
        const message = `${row.addressRow} - отсутствует обязательная колонка '${type}'`
        throw newException(ExceptionName.IMPORT_XLSX_PARSE, { message })
      }
      return undefined
    }
    const value = reader(cell)
    if (!value) {
      if (required) {
        const message = `${cell.name} - отсутствует обязательное значение или значение записано в неверном формате: '${cell.value.v}'`
        throw newException(ExceptionName.IMPORT_XLSX_PARSE, { message })
      }
      return undefined
    }
    return value
  }

  private readCellContentModifications(
    columns: Array<Column>,
    row: Row,
    type: SpecificationXLSXColumnType,
  ): Array<{ number: number, count: number }> {
    return this.getColumnsForType(columns, type)
      .map(col => {
        const number = col.modificationNumber
        const count = this.readCellNumber(this.getCellForColumn(row, col.addressColumn))
        if (number === undefined || count === undefined) {
          return undefined
        }
        return { number, count }
      })
      .filter(isNotUndefined)
  }

  private readCellString(cell: Cell | undefined): string | undefined {
    if (cell === undefined) {
      return undefined
    }
    const type = cell.value.t
    const value = cell.value.v
    if (type === "s" && typeof value === "string") {
      return value
    }
    return undefined
  }

  private readCellNumber(cell: Cell | undefined): number | undefined {
    if (cell === undefined) {
      return undefined
    }
    const type = cell.value.t
    const value = cell.value.v
    if (type === "n" && typeof value === "number") {
      return value
    }
    if (type === "s" && typeof value === "string") {
      return this.parseInt(value)
    }
    return undefined
  }

  private readCellBoolean(cell: Cell | undefined): boolean | undefined {
    if (cell === undefined) {
      return undefined
    }
    const type = cell.value.t
    const value = cell.value.v
    if (type === "b" && typeof value === "boolean") {
      return value
    }
    if (type === "s" && typeof value === "string") {
      const valueString = value.toLowerCase()
      return valueString === "да"
        || valueString === "yes"
        || valueString === "+"
        || valueString === "?"
    }
    if (type === "z") {
      return false
    }
    return undefined
  }

  private readCellDate(cell: Cell | undefined): Date | undefined {
    if (cell === undefined) {
      return undefined
    }
    const type = cell.value.t
    const value = cell.value.v
    if (type === "d" && value instanceof Date) {
      return value
    }
    if ((type === "s" && typeof value === "string") || (type === "n" && typeof cell.value.w === "string")) {
      // @ts-ignore
      const walue: string = type === "n" ? cell.value.w : value
      const [ sDay, sMonth, sYear ] = walue.split(new RegExp("[./]"))
      const [ day, month, year ] = [ this.parseInt(sDay), this.parseInt(sMonth), this.parseInt(sYear) ]
      if (day !== undefined && month !== undefined && year !== undefined) {
        return new Date(year, month - 1, day)
      }
    }
    return undefined
  }

  private isRowSpecificationAtSpecification(columns: Array<Column>, row: Row): boolean {
    const cell = this.getCellForType(columns, row, SpecificationXLSXColumnType.specificationId)
    const specificationId = this.readCellNumber(cell)
    return specificationId !== undefined
  }

  private getCellForType(columns: Array<Column>, row: Row, type: SpecificationXLSXColumnType): Cell | undefined {
    const column = this.getColumnsForType(columns, type)[0]
    if (!column) {
      return undefined
    }
    return this.getCellForColumn(row, column.addressColumn)
  }

  private getColumnsForType(columns: Array<Column>, type: SpecificationXLSXColumnType): Array<Column> {
    return columns
      .filter(column => column.type === type)
  }

  private getCellForColumn(row: Row, column: string): Cell | undefined {
    for (const cell of row.value) {
      if (cell.address.column === column) {
        return cell
      }
    }
    return undefined
  }

  private detectColumn(cell: Cell): Column | undefined {
    const addressColumn = cell.address.column
    const value = this.readCellString(cell)?.toLowerCase()
    if (value === undefined) {
      return undefined
    }
    switch (value) {
      case "артикул": return { type: SpecificationXLSXColumnType.article, addressColumn }
      case "наименование": return { type: SpecificationXLSXColumnType.name, addressColumn }
      case "конфигурация": return { type: SpecificationXLSXColumnType.configuration, addressColumn }
      case "цвет": return { type: SpecificationXLSXColumnType.color, addressColumn }
      case "признак": return { type: SpecificationXLSXColumnType.attn, addressColumn }
      case "ответственный": return { type: SpecificationXLSXColumnType.responsible, addressColumn }
      case "поставщик": return { type: SpecificationXLSXColumnType.supplier, addressColumn }
      case "метка для попадания в упрощенную спецификацию": return { type: SpecificationXLSXColumnType.simplified, addressColumn }
      case "раздел для работы": return { type: SpecificationXLSXColumnType.category, addressColumn }
      case "технологический запас, %": return { type: SpecificationXLSXColumnType.reserve, addressColumn }
      case "дата изменения поля": return { type: SpecificationXLSXColumnType.dateModified, addressColumn }
      case "примечание": return { type: SpecificationXLSXColumnType.note, addressColumn }
      case "вложенная спецификация": return { type: SpecificationXLSXColumnType.specificationId, addressColumn }
      case "тип компонента": return { type: SpecificationXLSXColumnType.componentType, addressColumn }
      case "part Reference": return { type: SpecificationXLSXColumnType.partReference, addressColumn }
      case "value": return { type: SpecificationXLSXColumnType.value, addressColumn }
    }
    if (value.startsWith("количество исп")) {
      const numberStr = value.slice("количество исп".length)
      const number = Number.parseInt(numberStr)
      if (isFinite(number)) {
        return {
          type: SpecificationXLSXColumnType.modificationN,
          modificationNumber: number,
          addressColumn,
        }
      }
    }
    return undefined
  }

  private parseInt(value: string): number | undefined {
    const number = Number.parseInt(value)
    if (isFinite(number)) {
      return number
    }
    return undefined
  }

  private yield(): Promise<void> {
    return promisify<void>(scheduleMacroTask)(() => void 0)
  }

  private readonly ROW_NUMBER_HEADER = 1
  private readonly ROW_NUMBER_START_CONTENT = 3
}

type Sheet = {
  name: string
  value: WorkSheet
}

type Address = {
  column: string
  row: string
}

type Cell = {
  name: string
  address: Address
  value: CellObject
}

type Row = {
  addressRow: string
  value: Array<Cell>
}

type Column = {
  type: SpecificationXLSXColumnType
  addressColumn: string
  modificationNumber?: number
}
