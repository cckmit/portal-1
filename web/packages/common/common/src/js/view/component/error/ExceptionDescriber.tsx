import { ElementType, useMemo } from "react"
import { observer } from "mobx-react-lite"
import { Exception } from "@protei-libs/exception"
import { describeException } from "../../../infrastructure"

type Props = Exception & {
  render: (header: string, description: string | undefined) => ElementType | JSX.Element | null
}

export const ExceptionDescriber = observer(function ExceptionDescriber(props: Props) {
  const { render, ...exception } = props
  const { header, description } = useMemo(() => describeException(exception), [ exception ])

  return (<>
    {render(header, description)}
  </>)
})
