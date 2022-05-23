import { AnyToVoidFunction } from "@protei-libs/types"
import { useCallback, useState } from "react"

export function useForceUpdate(): AnyToVoidFunction {
  const [, setTrigger] = useState(false)

  return useCallback(() => {
    setTrigger((trigger) => !trigger)
  }, [])
}
