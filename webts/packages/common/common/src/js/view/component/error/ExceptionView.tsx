import { observer } from "mobx-react-lite"
import { Exception } from "@protei-libs/exception"
import { ExceptionDescriber } from "./ExceptionDescriber"

type Props = {
  exception: Exception
}

export const ExceptionView = observer(function ExceptionView(props: Props) {
  const { exception } = props

  return (
    <ExceptionDescriber {...exception} render={(header, description) => (
      <div className="alert alert-danger mb-0" style={{ width: "fit-content" }}>
        <i className="fa-solid fa-hexagon-exclamation mr-1"/>
        <strong>{header}</strong>
        {description && (
          <div>{description}</div>
        )}
      </div>
    )}/>
  )
})
