import { observer } from "mobx-react-lite"
import { Routes, Route } from "react-router-dom"
import { getPath, HashFactoryRouter } from "@protei-portal/common-router"
import { Paths } from "../model/Paths"
import { TableComponent } from "./specifications/table/TableComponent"
import { ImportComponent } from "./specifications/import/ImportComponent"

export const DeliveryComponent = observer(function DeliveryComponent() {
  return (
    <HashFactoryRouter>
      <Routes>
        <Route path={getPath(Paths.c.delivery.c.specifications)} element={<TableComponent/>} />
        <Route path={getPath(Paths.c.delivery.c.specifications.c.import)} element={<ImportComponent/>} />
      </Routes>
    </HashFactoryRouter>
  )
})
