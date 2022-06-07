import { observer } from "mobx-react-lite"
import { classesOf } from "@protei-libs/react"
import { useLang } from "@protei-portal/common-lang"
import {
  ExceptionView,
  IndeterminateCircleLoading,
  isProgressError,
  isProgressProcessing,
  isProgressReady,
} from "@protei-portal/common"
import { specificationsImportStore } from "../../../store"

export const ImportPartProgressComponent = observer(function ImportPartProgressComponent() {
  const lang = useLang()
  const importProgress = specificationsImportStore.progress

  if (isProgressReady(importProgress)) {
    return null
  }

  return (
    <div className="container-fluid padding-15">
      <div className="card card-transparent no-margin">
        <div className="card-body">
          {isProgressProcessing(importProgress) && (
            <div className="row">
              <div className="col-12">
                <IndeterminateCircleLoading/>
              </div>
            </div>
          )}
          {isProgressError(importProgress) && (
            <div className="row">
              <div className="col-12">
                <ExceptionView exception={importProgress.exception}/>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  )
})
