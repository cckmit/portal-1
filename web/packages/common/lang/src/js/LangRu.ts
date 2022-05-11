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
}

export const langRu = new LangRu()
