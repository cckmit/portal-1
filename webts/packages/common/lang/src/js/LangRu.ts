/* eslint-disable @typescript-eslint/explicit-module-boundary-types */
import { Lang } from "./Lang"

export class LangRu implements Lang {
  applicationName = () => "SMSFW-ui"
  downloadLogs = () => "Скачать логи"
  buttonCreate = () => "Создать"
  buttonModify = () => "Изменить"
  buttonApply = () => "Применить"
  buttonSave = () => "Сохранить"
  buttonCancel = () => "Отменить"
  buttonReset = () => "Сбросить"
  buttonImport = () => "Импорт"
  buttonAdd = () => "Добавить"
  buttonRemove = () => "Удалить"
  deliverySpecificationsName = () => "Наименование спецификации"
  deliverySpecificationsImportFile = () => "Файл спецификации"
  deliverySpecificationsImportFileSelect = () => "Выбрать XLSX файл"
  deliverySpecificationsImportTitle = () => "Импорт спецификации"
  deliverySpecificationsImportLengthDetails = () => "Количество деталей"
  deliverySpecificationsImportLengthSpecifications = () => "Количество спецификаций"
  deliverySpecificationsImportLengthErrors = () => "Количество ошибок"
  deliverySpecificationsImportAlertHasErrors = () => "Файл спецификации содержит ошибки"
}

export const langRu = new LangRu()
