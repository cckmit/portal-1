import { useCallback } from "react"
import { useNavigate } from "react-router-dom"
import { observer } from "mobx-react-lite"
import { classesOf } from "@protei-libs/react"
import { useLang } from "@protei-portal/common-lang"
import { Button, isProgressProcessing } from "@protei-portal/common"
import { specificationsImportStore } from "../../../store"
import { useIoCBinding } from "../../../ioc"
import { SpecificationsImportService, SpecificationsImportService$type } from "../../../service"

export const ImportPartActionsComponent = observer(function ImportPartActionsComponent() {
  const lang = useLang()
  const navigate = useNavigate()
  const specification = specificationsImportStore.specification
  const name = specification?.name ?? ""
  const details = specification?.details ?? []
  const specifications = specification?.specifications ?? []
  const createProgress = specificationsImportStore.progress
  const parseProgress = specificationsImportStore.parse.progress
  const specificationsImportService = useIoCBinding<SpecificationsImportService>(SpecificationsImportService$type)

  const canClose = !isProgressProcessing(createProgress) && !isProgressProcessing(parseProgress)
  const canImport = !isProgressProcessing(createProgress) && !isProgressProcessing(parseProgress) && !!name
    && (details.length > 0 || specifications.length > 0) && !specificationsImportService.hasParseErrors()

  const onClose = useCallback(() => {
    navigate(-1)
    specificationsImportService.reset()
  }, [ specificationsImportService, navigate ])

  const onImport = useCallback(() => {
    void (async () => {
      if (specificationsImportService.hasParseErrors()) {
        return
      }
      await specificationsImportService.import()
      navigate(-1)
    })()
  }, [ specificationsImportService, navigate ])

  return (
    <div className="container-fluid padding-15">
      <div className="card card-transparent no-margin">
        <div className="card-body">
          <Button className="btn btn-complete"
                  value={lang.buttonCreate()}
                  onClick={onImport}
                  disabled={!canImport}/>
          &nbsp;
          <Button className="btn btn-default"
                  value={lang.buttonCancel()}
                  onClick={onClose}
                  disabled={!canClose}/>
        </div>
      </div>
    </div>
  )
})
