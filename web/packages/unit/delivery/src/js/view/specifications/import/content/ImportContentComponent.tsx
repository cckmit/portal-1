import { useCallback } from "react"
import { observer } from "mobx-react-lite"
import { Input, InputFile } from "@protei-portal/common"
import { useLang } from "@protei-portal/common-lang"
import { useIoCBinding } from "../../../../ioc"
import { specificationsCreateStore } from "../../../../store"
import { SpecificationsImportService, SpecificationsImportService$type } from "../../../../service"

export const ImportContentComponent = observer(function ImportContentComponent() {
  const lang = useLang()
  const specification = specificationsCreateStore.specification
  const name = specification?.name
  const specificationsImportService = useIoCBinding<SpecificationsImportService>(SpecificationsImportService$type)

  const setName = useCallback((name: string) => {
    specificationsImportService.setName(name)
  }, [ specificationsImportService ])

  const onFiles = useCallback((files: Array<File>) => {
    const file = files[0]
    if (file === undefined) {
      return
    }
    specificationsImportService.importFileXLSX(file)
  }, [ specificationsImportService ])

  return (
    <div className="card-body no-padding">
      <div className="container-fluid padding-15">
        <div className="card card-transparent no-margin">
          <div className="card-body">
            <div className="row">
              <div className="form-group col-12 col-md-6">
                <label>Наименование спецификации</label>
                <Input value={name}
                       onChangeFunction={setName} />
              </div>
            </div>
            <div className="row">
              <div className="form-group col-12 col-md-6">
                <label>Файл спецификации</label>
                <div>
                  <InputFile onFiles={onFiles}
                             accept="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet">
                    <i className="far fa-file-excel"/>
                    &nbsp;
                    <span className="bold">Выбрать XLSX файл</span>
                  </InputFile>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
})
