import { observer } from "mobx-react-lite"
import { ImportPartFormComponent } from "./ImportPartFormComponent"
import { ImportPartParseResultComponent } from "./ImportPartParseResultComponent"
import { ImportPartProgressComponent } from "./ImportPartProgressComponent"
import { ImportPartActionsComponent } from "./ImportPartActionsComponent"

export const ImportComponent = observer(function ImportComponent() {

  return (
    <div className="card card-transparent no-margin">
      <div className="card-body no-padding container-child-striped-gray-white">
        <ImportPartFormComponent/>
        <ImportPartParseResultComponent/>
        <ImportPartProgressComponent/>
        <ImportPartActionsComponent/>
      </div>
    </div>
  )
})
