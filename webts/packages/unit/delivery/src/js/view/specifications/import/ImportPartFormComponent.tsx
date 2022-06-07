import { useCallback } from "react"
import { observer } from "mobx-react-lite"
import { classesOf } from "@protei-libs/react"
import { useLang } from "@protei-portal/common-lang"
import { Input, InputFile, isProgressProcessing } from "@protei-portal/common"
import { useIoCBinding } from "../../../ioc"
import { specificationsImportStore } from "../../../store"
import { SpecificationsImportService, SpecificationsImportService$type } from "../../../service"

export const ImportPartFormComponent = observer(function ImportPartFormComponent() {
  const lang = useLang()
  const specification = specificationsImportStore.specification
  const name = specification?.name ?? ""
  const importProgress = specificationsImportStore.progress
  const parseProgress = specificationsImportStore.parse.progress
  const specificationsImportService = useIoCBinding<SpecificationsImportService>(SpecificationsImportService$type)
  const isFormDisabled = isProgressProcessing(importProgress)
  const isParseDisabled = isFormDisabled || isProgressProcessing(parseProgress)

  const setName = useCallback((name: string) => {
    specificationsImportService.setName(name)
  }, [ specificationsImportService ])

  const onFiles = useCallback((files: Array<File>) => {
    const file = files[0]
    if (file === undefined) {
      return
    }
    void specificationsImportService.parseFileXLSX(file)
  }, [ specificationsImportService ])

  return (
    <div className="container-fluid padding-15">
      <div className="card card-transparent no-margin">
        <div className="card-body">
          <div className="row">
            <div className="form-group col-12 col-md-6">
              <label>{lang.deliverySpecificationsName()}</label>
              <Input value={name}
                     onChangeFunction={setName}
                     state={isFormDisabled ? "disabled" : undefined}/>
            </div>
          </div>
          <div className="row">
            <div className="form-group col-12 col-md-6">
              <label>{lang.deliverySpecificationsImportFile()}</label>
              <div>
                <InputFile onFiles={onFiles}
                           accept="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                           state={isParseDisabled ? "disabled" : undefined}>
                  <i className="far fa-file-excel"/>
                  &nbsp;
                  <span className="bold">{lang.deliverySpecificationsImportFileSelect()}</span>
                </InputFile>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
})
