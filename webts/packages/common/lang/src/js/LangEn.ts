/* eslint-disable @typescript-eslint/explicit-module-boundary-types */
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
  deliverySpecificationsName = () => "Specification name"
  deliverySpecificationsImportFile = () => "Specification file"
  deliverySpecificationsImportFileSelect = () => "Choose XLSX file"
  deliverySpecificationsImportTitle = () => "Specification import"
  deliverySpecificationsImportLengthDetails = () => "Details"
  deliverySpecificationsImportLengthSpecifications = () => "Specifications"
  deliverySpecificationsImportLengthErrors = () => "Errors"
  deliverySpecificationsImportAlertHasErrors = () => "Specification file contains errors"
}

export const langEn = new LangEn()
