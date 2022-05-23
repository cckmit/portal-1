import { observer } from "mobx-react-lite"
import { IndeterminateCircleLoading } from "../../widget"

export const ModuleLoading = observer(function ModuleLoading() {

  return (
    <div className="card no-margin card-transparent">
      <div className="card-body no-margin">
        <IndeterminateCircleLoading/>
      </div>
    </div>
  )
})
