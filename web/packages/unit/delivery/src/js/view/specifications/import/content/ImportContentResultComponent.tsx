import { useCallback } from "react"
import { useNavigate } from "react-router-dom"
import { observer } from "mobx-react-lite"
import { Button } from "@protei-portal/common"
import { useLang } from "@protei-portal/common-lang"
import { useIoCBinding } from "../../../../ioc"
import { specificationsCreateStore, specificationsImportStore } from "../../../../store"
import {
  SpecificationsCreateService,
  SpecificationsCreateService$type,
  SpecificationsImportService,
  SpecificationsImportService$type,
} from "../../../../service"

export const ImportContentResultComponent = observer(function ImportContentResultComponent() {
  const lang = useLang()
  const navigate = useNavigate()
  const specification = specificationsCreateStore.specification
  const name = specification?.name ?? ""
  const details = specification?.details ?? []
  const specifications = specification?.specifications ?? []
  const errors = specificationsImportStore.errors
  const specificationsCreateService = useIoCBinding<SpecificationsCreateService>(SpecificationsCreateService$type)
  const specificationsImportService = useIoCBinding<SpecificationsImportService>(SpecificationsImportService$type)
  const canImport = !!name && (details.length > 0 || specifications.length > 0)

  const onClose = useCallback(() => {
    navigate(-1)
    specificationsCreateService.reset()
    specificationsImportService.reset()
  }, [ specificationsCreateService, specificationsImportService, navigate ])

  const onCreate = useCallback(async () => {
    await specificationsCreateService.create()
    navigate(-1)
  }, [ specificationsCreateService, navigate ])


  if (details.length == 0 && specifications.length == 0 && errors.length == 0) {
    return (
      <div className="container-fluid padding-15 bg-white">
        <div className="card card-transparent no-margin">
          <div className="card-footer no-border bg-transparent">
            <Button className="btn btn-default" value={lang.buttonCancel()} onClick={onClose}/>
          </div>
        </div>
      </div>
    )
  }

  return (
    <div className="container-fluid padding-15 bg-white">
      <div className="card card-transparent no-margin">
        <div className="card-header">
          <div className="card-title">
            {lang.deliverySpecificationsImportTitle()}
          </div>
        </div>
        <div className="card-body">
          <div className="row mt-2">
            <div className="col-lg-2 col-md-3 col-12">
              <h5 className="bold no-margin">{details.length}</h5>
              <p className="no-margin">{lang.deliverySpecificationsImportLengthDetails()}</p>
            </div>
            <div className="col-lg-2 col-md-3 col-12">
              <h5 className="bold no-margin">{specifications.length}</h5>
              <p className="no-margin">{lang.deliverySpecificationsImportLengthSpecifications()}</p>
            </div>
            <div className="col-lg-2 col-md-3 col-12">
              <h5 className="bold no-margin">{errors.length}</h5>
              <p className="no-margin">{lang.deliverySpecificationsImportLengthErrors()}</p>
            </div>
          </div>
          {errors.length > 0 && (
            <div className="row mt-3">
              {errors.map(error => (
                // eslint-disable-next-line react/jsx-key
                <div className="col-12 pt-1 pb-1">
                  <span className="text-danger bold">{error}</span>
                </div>
              ))}
            </div>
          )}
        </div>
        <div className="card-footer no-border bg-transparent pt-0">
          <Button className="btn btn-complete" value={lang.buttonCreate()} onClick={onCreate} disabled={!canImport} />
          &nbsp;
          <Button className="btn btn-default" value={lang.buttonCancel()} onClick={onClose} />
        </div>
      </div>
    </div>
  )
})
