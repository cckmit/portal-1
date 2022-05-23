import { observer } from "mobx-react-lite"
import { useLang } from "@protei-portal/common-lang"
import {
  ExceptionDescriber,
  IndeterminateBarLoading,
  isProgressError,
  isProgressProcessing,
  ProgressError,
  ProgressProcessing,
} from "@protei-portal/common"

type Props = {
  progress: ProgressProcessing | ProgressError
}

export const ImportContentStateComponent = observer(function ImportContentStateComponent(props: Props) {
  const { progress } = props
  const lang = useLang()

  return (
    <div className="container-fluid padding-15 bg-white">
      <div className="card card-transparent no-margin">
        <div className="card-header">
          <div className="card-title">
            {lang.deliverySpecificationsImportTitle()}
          </div>
        </div>
        <div className="card-body">
          {isProgressProcessing(progress) && (
            <div className="row">
              <div className="col-12 col-md-4 mt-3">
                <IndeterminateBarLoading/>
              </div>
            </div>
          )}
          {isProgressError(progress) && (
            <ExceptionDescriber {...progress.exception} render={(header, description) => (
              <div className="alert alert-danger mb-0">
                <strong>{header}</strong>
                {description && (
                  <div>{description}</div>
                )}
              </div>
            )}/>
          )}
        </div>
      </div>
    </div>
  )
})
