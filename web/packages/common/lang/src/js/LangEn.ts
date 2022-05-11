import { Lang } from "./Lang"

export class LangEn implements Lang {
  applicationName = () => "SMSFW-ui"
  downloadLogs = () => "Download logs"
  buttonCreate = () => "Create"
  buttonModify = () => "Edit"
  buttonApply = () => "Apply"
  buttonSave = () => "Save"
  buttonCancel = () => "Cancel"
  buttonReset = () => "Reset"
  buttonImport = () => "Import"
  buttonAdd = () => "Add"
  buttonRemove = () => "Remove"
}

export const langEn = new LangEn()
