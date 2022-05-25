import { memo } from "react"

export const IndeterminateBarLoading = memo(function IndeterminateBarLoading() {

  return (
    <div className="progress">
      <div className="progress-bar-indeterminate"/>
    </div>
  )
})
