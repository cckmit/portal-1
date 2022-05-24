import { useCallback, useEffect } from "react"
import { observer } from "mobx-react-lite"
import { Input, InputFile, isProgressError, isProgressProcessing, isProgressReady } from "@protei-portal/common"
import { useLang } from "@protei-portal/common-lang"
import { useIoCBinding } from "../../../../ioc"
import { specificationsCreateStore, specificationsImportStore } from "../../../../store"
import {
  SpecificationsCreateService,
  SpecificationsCreateService$type,
  SpecificationsImportService,
  SpecificationsImportService$type,
} from "../../../../service"
import { ImportContentStateComponent } from "./ImportContentStateComponent"
import { ImportContentResultComponent } from "./ImportContentResultComponent"

export const ImportContentComponent = observer(function ImportContentComponent() {
  const lang = useLang()
  const specification = specificationsCreateStore.specification
  const name = specification?.name ?? ""
  const createProgress = specificationsCreateStore.progress
  const importProgress = specificationsImportStore.progress
  const specificationsCreateService = useIoCBinding<SpecificationsCreateService>(SpecificationsCreateService$type)
  const specificationsImportService = useIoCBinding<SpecificationsImportService>(SpecificationsImportService$type)
  const isFormDisabled = isProgressProcessing(createProgress)
  const isImportDisabled = isFormDisabled || isProgressProcessing(importProgress)

  useEffect(() => {
    specificationsCreateService.reset()
    specificationsImportService.reset()
  }, [ specificationsCreateService, specificationsImportService ])

  const setName = useCallback((name: string) => {
    specificationsCreateService.setName(name)
  }, [ specificationsCreateService ])

  const onFiles = useCallback((files: Array<File>) => {
    void (async () => {
      const file = files[0]
      if (file === undefined) {
        return
      }
      specificationsCreateService.reset({ keepName: true })
      specificationsImportService.reset()
      await specificationsImportService.importFileXLSX(file)
    })()
  }, [ specificationsCreateService, specificationsImportService ])

  return (
    <div className="card-body no-padding">
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
                             state={isImportDisabled ? "disabled" : undefined}>
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
      {(isProgressProcessing(importProgress) || isProgressError(importProgress)) && (
        <ImportContentStateComponent progress={importProgress}/>
      )}
      {isProgressReady(importProgress) && (
        <ImportContentResultComponent/>
      )}
    </div>
  )
})
