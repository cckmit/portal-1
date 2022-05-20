import { observer } from "mobx-react-lite"
import { Routes, Route } from "react-router-dom"
import { makeLogger } from "@protei-libs/logger"
import { getPath, HashFactoryRouter } from "@protei-portal/common-router"
import { BSOD, ErrorBoundary } from "@protei-portal/common"
import { Paths } from "../model"
import { TableComponent } from "./specifications/table/TableComponent"
import { ImportComponent } from "./specifications/import/ImportComponent"

export const DeliveryComponent = observer(function DeliveryComponent() {
  return (
    <ErrorBoundary fallback={BSOD} logger={makeLogger("portal.view")}>
      <HashFactoryRouter>
        <Routes>
          <Route path={getPath(Paths.c.delivery.c.specifications)} element={<TableComponent/>} />
          <Route path={getPath(Paths.c.delivery.c.specifications.c.import)} element={<ImportComponent/>} />
        </Routes>
      </HashFactoryRouter>
    </ErrorBoundary>
  )
})
