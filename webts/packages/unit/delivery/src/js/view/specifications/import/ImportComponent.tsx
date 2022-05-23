import { observer } from "mobx-react-lite"
import { ImportContentComponent } from "./content/ImportContentComponent"

export const ImportComponent = observer(function ImportComponent() {
  return (
    <div className="card card-transparent no-margin">
      <ImportContentComponent/>
    </div>
  )
})
