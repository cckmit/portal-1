export interface Lang {
  applicationName(): string
  downloadLogs(): string
  buttonCreate(): string
  buttonModify(): string
  buttonApply(): string
  buttonSave(): string
  buttonCancel(): string
  buttonReset(): string
  buttonImport(): string
  buttonAdd(): string
  buttonRemove(): string
  deliverySpecificationsName(): string
  deliverySpecificationsImportFile(): string
  deliverySpecificationsImportFileSelect(): string
  deliverySpecificationsImportTitle(): string
  deliverySpecificationsImportLengthDetails(): string
  deliverySpecificationsImportLengthSpecifications(): string
  deliverySpecificationsImportLengthErrors(): string
}
