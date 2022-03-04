import { observer } from "mobx-react-lite"
import { useEffect } from "react"
import { useIoCBinding } from "../ioc"
import { test1Store } from "../store/Test1Store"
import { Test1Service, Test1Service$type } from "../service/Test1Service"

export const Test1Component = observer(function Test1Component() {
  const counter = test1Store.counter
  const test1service = useIoCBinding<Test1Service>(Test1Service$type)

  useEffect(() => {
    test1service.start()
    return () => {
      test1service.stop()
    }
  }, [test1service])

  return (
    <div>
      <div>Test 1 component</div>
      <div>Counter: {counter}</div>
    </div>
  )
})
