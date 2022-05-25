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
  bsodTitle1 = () => "Данное приложение столкнулось с непредвиденной ошибкой и нуждается в перезагрузке"
  bsodTitle2 = () => "Ниже указано краткое описание ошибки"
  bsodActionReload = () => "Перезагрузить приложение"
  bsodActionDownloadLogs = () => "Скачать файл логов"
  exceptionAny = () => "Произошла ошибка"
  exceptionNative = () => "Произошла неожиданная ошибка"
  exceptionReact = () => "Произошла ошибка"
  exceptionUnavailable = () => "Невозможно совершить действие"
  exceptionIllegalState = () => "Неожиданное состояние приложения"
  exceptionIllegalArgument = () => "Неожиданное состояние приложения"
  exceptionApi = () => "Запрос на сервер закончился ошибкой"
  exceptionApiError = () => "Запрос на сервер закончился ошибкой"
  exceptionApiOffline = () => "Нет доступа к интернету"
  exceptionApiTimeout = () => "Время ожидания ответа сервера истекло"
  exceptionApiParse = () => "Не удалось разобрать ответ сервера"
  exceptionImportXlsxParse = () => "Не удалось разобрать XLSX файл"
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
