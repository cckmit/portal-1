import { observer } from "mobx-react-lite"
import { ImportContentComponent } from "./content/ImportContentComponent"
import { ImportFooterComponent } from "./footer/ImportFooterComponent"

export const ImportComponent = observer(function ImportComponent() {
  return (
    <div className="card card-transparent no-margin card-with-fixable-footer">
      <div className="card-body">
        <ImportContentComponent/>
        <ImportFooterComponent/>
      </div>
    </div>
  )
})
