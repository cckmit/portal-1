import { observer } from "mobx-react-lite"
import { useModuleLoader } from "@protei-libs/module"
import { unitDeliveryModule } from "../module"

export const DeliveryComponentAsync = observer(function DeliveryComponentAsync() {
  const module = useModuleLoader({ moduleLoader: unitDeliveryModule.loader })
  return module ? <module.DeliveryComponent /> : <div>Loading...</div>
})
