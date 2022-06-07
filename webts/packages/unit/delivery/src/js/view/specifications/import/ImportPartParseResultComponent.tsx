import { observer } from "mobx-react-lite"
import { classesOf } from "@protei-libs/react"
import { useLang } from "@protei-portal/common-lang"
import { ExceptionView, isProgressError, isProgressReady } from "@protei-portal/common"
import { specificationsImportStore } from "../../../store"

export const ImportPartParseResultComponent = observer(function ImportPartParseResultComponent() {
  const lang = useLang()
  const specification = specificationsImportStore.specification
  const details = specification?.details ?? []
  const specifications = specification?.specifications ?? []
  const parseProgress = specificationsImportStore.parse.progress
  const parseErrors = specificationsImportStore.parse.errors

  if (isProgressReady(parseProgress) && details.length === 0 && specifications.length === 0) {
    return null
  }

  return (
    <div className="container-fluid padding-15">
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
              <h5 className="bold no-margin">{parseErrors.length}</h5>
              <p className="no-margin">{lang.deliverySpecificationsImportLengthErrors()}</p>
            </div>
          </div>
          {parseErrors.length > 0 && (<>
            <div className="row mt-3">
              {parseErrors.map(error => (
                // eslint-disable-next-line react/jsx-key
                <div className="col-12 pt-1 pb-1">
                  <span className="text-danger bold">{error}</span>
                </div>
              ))}
            </div>
            <div className="row mt-3">
              <div className="col-12">
                <div className="alert alert-danger mb-0" style={{ width: "fit-content" }}>
                  {lang.deliverySpecificationsImportAlertHasErrors()}
                </div>
              </div>
            </div>
          </>)}
          {isProgressError(parseProgress) && (
            <div className="row mt-3">
              <div className="col-12">
                <ExceptionView exception={parseProgress.exception}/>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  )
})
